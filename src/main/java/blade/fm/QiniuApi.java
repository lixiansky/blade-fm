package blade.fm;

import java.io.File;

import blade.kit.log.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * 各种客户端获取工厂
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
public class QiniuApi {
	
	private final static String ACCESSKEY = "Wey8eOUeoHVXC1NwmLLwbvDES-3PIOlSpYg0umbz";
	private final static String SECRETKEY = "N51TJuZugUlTeBDlEDfqjuF2SEQ94UEmZMesCKLA";
	
	private final static Auth auth = Auth.create(ACCESSKEY, SECRETKEY);
	
	private final static Logger log = Logger.getLogger(QiniuApi.class);
	
	private static UploadManager uploadManager = new UploadManager();
	
	public static String getUploadToken(){
		StringMap policy = new StringMap();
		policy.putNotEmpty(
				"returnBody",
				"{\"key\":$(key),\"hash\":$(etag),\"w\":$(imageInfo.width),\"h\":$(imageInfo.height),\"format\":$(imageInfo.format)}");
		
		return auth.uploadToken("blade-blog", null, 3600, policy);
	}
	
	public static String getUrlByKey(String key){
		return "http://7xjr0p.com1.z0.glb.clouddn.com/" + key; 
	}
	
	public static String uploadFile(File file){
		try {
			
			String fileName = file.getName();
			
			Response res = uploadManager.put(file, fileName, getUploadToken());
			String body = res.bodyString();
			
			log.info("body = " + body);
			
			JSONObject jsonObject = JSON.parseObject(body);
			if(null != jsonObject){
				return jsonObject.getString("key");
			}
		} catch (QiniuException e) {
			Response r = e.response;
	        // 请求失败时简单状态信息
	        log.error(r.toString());
	        try {
	            // 响应的文本信息
	            log.error(r.bodyString());
	        } catch (QiniuException e1) {
	            //ignore
	        }
		}
		return null;
	}
}
