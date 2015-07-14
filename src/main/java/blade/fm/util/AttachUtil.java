package blade.fm.util;

import blade.kit.FileKit;

/**
 * 附件工具类
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
public class AttachUtil {
	
	public static String getMusicKey(Integer uid, String song_path, String key){
		String ext = FileKit.getExtension(song_path);
		return "user/" + uid + "/music/" + key + ext;
	}
	
	public static String getRadioKey(Integer uid, String song_path, String key){
		String ext = FileKit.getExtension(song_path);
		return "user/" + uid + "/radio/" + key + ext;
	}
	
	public static String getMusicCoverKey(Integer uid, String song_path, String key){
		String ext = FileKit.getExtension(song_path);
		return "user/" + uid + "/music_cover/" + key + "_cover" + ext;
	}
	
	public static String getPicKey(Integer uid, String pic_path, String key){
		String ext = FileKit.getExtension(pic_path);
		return "user/" + uid + "/images/" + key + ext;
	}
	
	
}
