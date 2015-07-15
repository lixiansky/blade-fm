package blade.fm.route.admin;

import java.io.File;
import java.util.Map;

import blade.Blade;
import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.cache.CacheManager;
import blade.fm.Constant;
import blade.fm.route.BaseRoute;
import blade.fm.service.SettingService;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSONObject;

/**
 * 系统后台设置
 * @author:rex
 * @date:2014年10月13日
 * @version:1.0
 */
@Path
public class SysRoute extends BaseRoute {
	
	@Inject
	private SettingService settingService;
	
	/**
	 * 系统设置
	 */
	@Route("/admin/sys/setting")
	public String setting(Request request) {
		Map<String, String> setting = settingService.getAllSetting();
		request.attribute("setting", setting);
		return "/admin/setting";
	}

	/**
	 * 保存系统配置
	 */
	@Route("/admin/sys/save_setting")
	public void save_setting(Request request, Response response) {
		String site_title = request.query("site_title");
		String site_keywords = request.query("site_keywords");
		String site_description = request.query("site_description");
		String sina_weibo = request.query("sina_weibo");
		String tencent_weibo = request.query("tencent_weibo");
		boolean flag = false;
		try {
			settingService.save("site_title", site_title);
			settingService.save("site_keywords", site_keywords);
			settingService.save("site_description", site_description);
			settingService.save("sina_weibo", sina_weibo);
			settingService.save("tencent_weibo", tencent_weibo);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}

	/**
	 * 清空程序缓存
	 */
	@Route("/admin/sys/cleanCache")
	public void cleanCache(Request request, Response response) {
		
		CacheManager cm = CacheManager.getInstance();
		cm.removeAll();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", true);
		response.json(jsonObject.toJSONString());
	}
	
	/**
	 * 清空临时目录
	 */
	@Route("/admin/sys/cleanTemp")
	public void cleanTemp(Request request, Response response) {
		String temp = Blade.webRoot() + File.separator + Constant.UPLOAD_FOLDER + File.separator + "temp" + File.separator;
		File tempDir = new File(temp);
		if (tempDir.isDirectory()) {
			File[] files = tempDir.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", true);
		response.json(jsonObject.toJSONString());
	}
	
}
