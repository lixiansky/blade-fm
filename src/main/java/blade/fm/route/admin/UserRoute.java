package blade.fm.route.admin;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.UserService;
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
public class UserRoute extends BaseRoute {

	@Inject
	private UserService userService;
	
	/**
	 * 后台首页
	 */
	@Route("/admin/user/index")
	public String index(Request request) {
		Page<Map<String, Object>> userPage = userService.getPageMapList(null, null, null, page, pageSize, "uid desc");
		request.attribute("userPage", userPage);
		return "/admin/user";
	}

	/**
	 * 删除用户
	 */
	@Route("/admin/user/del")
	public void del(Request request,Response response) {
		Integer uid = request.queryToInt("uid");
		int count = userService.update(uid, null, null, null, 0);
		boolean flag = count > 0;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}

	/**
	 * 保存用户
	 */
	@Route("/admin/user/save")
	public String save(Request request, Response response) {
		Integer uid = request.queryToInt("uid");
		if (null != uid) {
			String nickname = request.query("nickname");
			Long space_size = request.queryToLong("space_size");
			int count = userService.update(uid, null, nickname, space_size, null);
			boolean flag = count > 0;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			return null;
		}
		return "/admin/edit_user";
	}

	/**
	 * 编辑用户
	 */
	@Route("/admin/users/:uid")
	public String edit_user(Request request) {
		Integer uid = request.pathParamToInt("uid");
		// 编辑
		if (null != uid) {
			Map<String, Object> user = userService.getMap(null, uid);
			request.attribute("user", user);
		}
		return "/admin/edit_user";
	}
	
}
