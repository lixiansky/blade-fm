package blade.fm.route;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import blade.Blade;
import blade.BladeWebContext;
import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.service.HashService;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSON;
import com.baidu.ueditor.ActionEnter;

/**
 * 文件上传
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Path("/upload")
public class UploadController extends BaseRoute {

	private Logger logger = Logger.getLogger(UploadController.class);
	private static final int BUFFER_SIZE = 100 * 1024;
	private String savePath;
	private String tempPath;
	private String imagePath;
	private String mp3Path;
	private String videoPath;
	private String filePath;
	private String domainBase;
	private Request request;
	@Inject
	private HashService hashService;

	private String rootDir;
	private String uplodDir = Blade.webRoot() + "/upload";
	private Integer uid = 1;

	public void init(){
		request = BladeWebContext.request();
		if (null == rootDir) {
			rootDir = request.servletPath();
			this.uid = 1;
			tempPath = rootDir + uplodDir + File.separator + "temp";
			imagePath = rootDir + uplodDir + File.separator + "images" + File.separator + this.uid;
			mp3Path = rootDir + uplodDir + File.separator + "mp3" + File.separator + this.uid;
			videoPath = rootDir + uplodDir + File.separator + "video" + File.separator + this.uid;
			filePath = rootDir + uplodDir + File.separator + "files" + File.separator + this.uid;
		}
		if (null == domainBase) {
			String ctx = request.contextPath();
			domainBase = request.scheme() + "://" + request.servletRequest().getServerName() + ":" + request.port() + ctx + "/";
		}
	}
	
	@Route("ueditor")
	public void ueditor(Request request, Response response) {
		response.header("Content-Type", "text/html");
		response.text(new ActionEnter(request.servletRequest()).exec());
	}

	/**
	 * 上传临时文件
	 * @throws Exception 
	 */
	@Route("temp")
	public void temp(Response response) throws Exception {
		init();
		savePath = tempPath;
		fileupload(savePath, response);
	}

	/**
	 * 上传用户目录文件
	 * @throws Exception 
	 */
	@Route("file")
	public void file(Response response) throws Exception {
		init();
		savePath = filePath;
		fileupload(savePath, response);
	}

	/**
	 * 上传图片文件
	 * @throws Exception 
	 */
	@Route("file/pic")
	public void pic(Response response) throws Exception {
		init();
		savePath = imagePath;
		fileupload(savePath, response);
	}

	/**
	 * 上传音乐文件
	 * @throws Exception
	 */
	@Route("file/mp3")
	public void mp3(Response response) throws Exception {
		init();
		savePath = mp3Path;
		fileupload(savePath, response);
	}

	/**
	 * 上传视频文件
	 * @throws Exception
	 */
	@Route("file/video")
	public void video(Response response) throws Exception {
		init();
		savePath = videoPath;
		fileupload(savePath, response);
	}

	private void appendFile(InputStream in, File destFile) {
		OutputStream out = null;
		try {
			// plupload 配置了chunk的时候新上传的文件append到文件末尾
			if (destFile.exists()) {
				out = new BufferedOutputStream(new FileOutputStream(destFile, true), BUFFER_SIZE);
			} else {
				out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
			}
			in = new BufferedInputStream(in, BUFFER_SIZE);

			int len = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * 文件上传处理
	 * @throws Exception 
	 */
	private void fileupload(String savePath, Response r) throws Exception {
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request.servletRequest());
			if (isMultipart) {

				String fileName = "";
				Integer chunk = 0, chunks = 0;

				//检查文件目录，不存在则创建
				File uploadDir = new File(savePath);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}

				DiskFileItemFactory diskFactory = new DiskFileItemFactory();
				// threshold 极限、临界值，即硬盘缓存 1M  
				diskFactory.setSizeThreshold(4 * 1024);

				ServletFileUpload upload = new ServletFileUpload(diskFactory);
				// 设置允许上传的最大文件大小（单位MB）
				upload.setSizeMax(1024 * 1048576);
				upload.setHeaderEncoding("UTF-8");

				try {
					List<FileItem> fileList = upload.parseRequest(request.servletRequest());
					Iterator<FileItem> it = fileList.iterator();
					while (it.hasNext()) {
						FileItem item = it.next();
						String name = item.getFieldName();
						InputStream input = item.getInputStream();
						if ("name".equals(name)) {
							fileName = Streams.asString(input);
							continue;
						}
						if ("chunk".equals(name)) {
							chunk = Integer.valueOf(Streams.asString(input));
							continue;
						}
						if ("chunks".equals(name)) {
							chunks = Integer.valueOf(Streams.asString(input));
							continue;
						}
						// 处理上传文件内容
						if (!item.isFormField()) {

							//目标文件
							File destFile = new File(savePath, fileName);

							logger.debug("[chunk=" + chunk + ",chunks=" + chunks + "]");

							//文件已存在删除旧文件（上传了同名的文件） 
							if (chunk == 0 && destFile.exists()) {
								destFile.delete();
								destFile = new File(savePath, fileName);
							}

							// 非分块
							if (chunk == 0 && chunks == 0) {

								String saveFilePath = getFileName(fileName);

								item.write(new File(saveFilePath));

								Map<String, Object> fileInfo = this.getResponse(saveFilePath, fileName, item.getSize());
								logger.info("非分块上传结果：" + fileInfo);
								r.json(JSON.toJSONString(fileInfo));
								return;
							}

							//合成文件
							appendFile(input, destFile);

							// 分块
							if (chunk == chunks - 1) {
								String saveFilePath = getFileName(fileName);
								boolean flag = destFile.renameTo(new File(saveFilePath));
								if (flag) {
									Map<String, Object> fileInfo = this.getResponse(saveFilePath, fileName,
											item.getSize());
									logger.info("分块上传结果：" + fileInfo);
									r.json(JSON.toJSONString(fileInfo));
								}
							} else {
								logger.info("还剩[" + (chunks - 1 - chunk) + "]个块文件");
							}
						}
					}
				} catch (FileUploadException ex) {
					logger.warn("上传文件失败：" + ex.getMessage());
					return;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 获取返回结果map
	 * @param saveFilePath
	 * @param fileName
	 * @param length
	 * @return
	 */
	private Map<String, Object> getResponse(String saveFilePath, String fileName, Long length) {
		Map<String, Object> fileInfo = CollectionKit.newHashMap();
		fileInfo.put("save_path", saveFilePath);
		fileInfo.put("save_name", saveFilePath.substring(saveFilePath.lastIndexOf(".") + 1));
		fileInfo.put("file_name", fileName);
		String url = domainBase + saveFilePath.replace(rootDir, "").replaceAll("\\\\", "/");
		fileInfo.put("url", url);
		fileInfo.put("key", saveFilePath.replace(rootDir, "").replaceAll("\\\\", "/"));
		fileInfo.put("length", length);
		return fileInfo;
	}

	/**
	 * 获取保存的文件名称
	 * @param fileName
	 * @return
	 */
	private String getFileName(String fileName) {
		// 扩展名格式：  
		String extName = FileKit.getExtension(fileName);

		// 生成文件名：  
		String dateStr = DateKit.dateFormat(new Date(), "yyyyMMddHHmmss");
		String random = StringKit.random(5);
		String saveFilePath = savePath + File.separator + dateStr + random + extName;
		return saveFilePath;
	}
	
}
