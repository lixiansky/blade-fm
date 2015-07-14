package blade.fm.route.admin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.RadioService;
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
@Path("/admin/radio")
public class RadioRoute extends BaseRoute {

	@Inject
	private RadioService radioService;
	@Inject
	private SpecialService specialService;

	/**
	 * 后台首页
	 */
	@Route("/index")
	public String index(Request request) {
		String title = request.query("title");
		Integer sid = request.queryToInt("sid");
		Integer status = request.queryToInt("status");
		Page<Map<String, Object>> radioPage = radioService.getPageMapList(uid, title, sid, status, page, pageSize,
				"id desc");
		request.attribute("pageMap", radioPage);
		return "/admin/radio";
	}

	/**
	 * 编辑电台
	 */
	@Route("/:id")
	public String edit_radio(Request request) {
		Integer id = request.pathParamToInt("id");
		// 编辑
		if (null != id) {
			Map<String, Object> radio = radioService.getMap(null, id);
			request.attribute("radio", radio);
		}
		List<Map<String, Object>> specialList = specialService.getList(null, 2, null, null, 1, "id desc");
		request.attribute("specialList", specialList);
		return "/admin/edit_radio";
	}

	/**
	 * 保存音乐
	 */
	@Route("/save")
	public String save(Request request, Response response) {
		String step = request.query("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer id = request.queryToInt("id");
			String title = request.query("title");
			Integer sid = request.queryToInt("sid");
			String url = request.query("url");

			boolean flag = false;
			if (null != id) {
				flag = radioService.update(id, title, sid, url);
			} else {
				flag = radioService.save(uid, title, sid, url);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			return null;
		}
		List<Map<String, Object>> specialList = specialService.getList(null, 2, null, null, 1, "id desc");
		request.attribute("specialList", specialList);
		return "/admin/edit_radio";
	}

	/**
	 * 删除电台
	 */
	@Route("/del")
	public void del(Request request, Response response) {
		Integer id = request.queryToInt("id");
		boolean flag = false;
		if (null != id) {
			flag = radioService.delete(id);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}

}
