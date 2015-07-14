package blade.fm.route.admin;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.FocusService;
import blade.plugin.sql2o.Page;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSONObject;

/**
 * 焦点图管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Path
public class FocusRoute extends BaseRoute {

	@Inject
	private FocusService focusService;

	/**
	 * 焦点图列表
	 */
	@Route("/admin/focus")
	public String index(Request request) {
		Page<Map<String, Object>> focusPage = focusService.getPageMapList(null, null, null, page, pageSize, "create_time desc");
		request.attribute("focusPage", focusPage);
		return "/admin/focus";
	}


	/**
	 * 编辑焦点图
	 */
	@Route("/admin/focus/:id")
	public String edit_focus(Request request) {
		Integer id = request.pathParamToInt("id");
		// 编辑
		if (null != id) {
			Map<String, Object> focusMap = focusService.getMap(null, id);
			request.attribute("focus", focusMap);
		}
		return "/admin/edit_focus";
	}

	/**
	 * 保存焦点图
	 */
	@Route("save")
	public String save(Request request, Response response) {
		String step = request.query("step");
		if (StringUtils.isNoneBlank(step) && step.equals("submit")) {
			Integer id = request.queryToInt("id");
			Integer type = request.queryToInt("type");
			String title = request.query("title");
			String introduce = request.query("introduce");
			String pic = request.query("pic");
			Integer status = request.queryToInt("status");
			boolean flag = false;
			uid = 1;
			if (null != id) {
				flag = focusService.update(id, title, introduce, pic, type, status) > 0;
			} else {
				flag = focusService.save(title, introduce, pic, type);
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			
			return null;
		}
		return "/admin/edit_focus";
	}
	
}
