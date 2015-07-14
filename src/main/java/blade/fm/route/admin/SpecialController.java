package blade.fm.route.admin;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.SpecialService;
import blade.plugin.sql2o.Page;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 用户后台
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Path
public class SpecialController extends BaseRoute {

	@Inject
	private SpecialService specialService;

	/**
	 * 后台首页
	 */
	@Route("/admin/special/index")
	public String index(Request request) {
		Page<Map<String, Object>> specialPage = specialService.getPageMapList(null, null, null, null, null, page,
				pageSize, "last_time desc");
		request.attribute("specialPage", specialPage);
		return "/admin/special";
	}

	/**
	 * 保存专辑
	 */
	@Route("/admin/special/save")
	public String save(Request request, Response response) {
		String step = request.query("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer id = request.queryToInt("id");
			Integer is_fine = request.queryToInt("is_fine");
			String title = request.query("title");
			String introduce = request.query("introduce");
			String cover = request.query("cover");
			Integer status = request.queryToInt("status");
			boolean flag = false;
			uid = 1;
			if (null != id) {
				flag = specialService.update(id, uid, title, introduce, cover, is_fine, status) > 0;
			} else {
				flag = specialService.save(uid, title, introduce, cover, is_fine, 1);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			return null;
		}
		return "/admin/edit_special";
	}

	/**
	 * 编辑专辑
	 */
	@Route("/admin/special/:sid")
	public String edit_special(Request request) {
		Integer sid = request.pathParamToInt("sid");
		// 编辑
		if (null != sid) {
			Map<String, Object> music = specialService.getMap(null, sid);
			request.attribute("special", music);
		}
		return "/admin/edit_special";
	}

	/**
	 * 禁用专辑
	 */
	@Route("/admin/special/enable")
	public void enable(Request request, Response response) {
		Integer sid = request.queryToInt("sid");
		Integer status = request.queryToInt("status");
		boolean flag = false;
		if (null != sid && null != status) {
			flag = specialService.enable(sid, status);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}
	
}
