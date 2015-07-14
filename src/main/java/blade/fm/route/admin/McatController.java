package blade.fm.route.admin;

import java.util.List;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.model.Mcat;
import blade.fm.route.BaseRoute;
import blade.fm.service.McatService;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSONObject;

/**
 * 分类管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Path("/admin/mcat")
public class McatController extends BaseRoute {

	@Inject
	private McatService mcatService;

	/**
	 * 分类列表
	 */
	@Route("index")
	public String index(Request request) {
		List<Mcat> mcatList = mcatService.getList(null);
		request.attribute("mcatList", mcatList);
		return "/admin/mcat";
	}

	/**
	 * 保存分类
	 */
	@Route("save")
	public void save(Request request, Response response) {
		Integer id = request.queryToInt("id");
		String name = request.query("name");
		boolean flag = false;
		if (null != id) {
			flag = mcatService.update(id, name, null);
		} else {
			flag = mcatService.save(name);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
		
	}

}
