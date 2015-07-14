package blade.fm.service;


/**
 * 文件管理接口
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
public interface FileService {

	/**
	 * 删除单个文件
	 * @param key
	 * @return 返回200成功
	 */
	void delete(String key);
	
	
	/**
	 * 上传文件
	 * @param key key名称
	 * @param filePath 物理路径
	 * @return
	 */
	void upload(String key, String filePath);
	
}
