package blade.fm.route.admin;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.AlbumService;
import blade.plugin.sql2o.Page;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSONObject;

/**
 * 图库管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Path("/admin/pic")
public class AlbumRoute extends BaseRoute {
	
	@Inject
	private AlbumService pictureService;
	
	/**
	 * 图库列表
	 */
	@Route("index")
	public String index(Request request, Response response) {
		String title = request.query("title");
		Integer status = request.queryToInt("status");
		Page<Map<String, Object>> picPage = pictureService.getPageMapList(uid, title, status, page, pageSize, "id desc");
		request.attribute("picPage", picPage);
		return "/admin/picture";
	}
	
	/**
	 * 保存图库
	 */
	@Route("save")
	public String save(Request request, Response response){
		
		String step = request.query("step");
		
		if(StringUtils.isNotBlank(step) && step.equals("submit")){
			Integer id = request.queryToInt("id");
			String title = request.query("title");
			String introduce = request.query("introduce");
			String pics = request.query("pics");
			String cover = request.query("cover");
			Integer status = request.queryToInt("status");
			boolean flag = false;
			if(null != id){
				flag = pictureService.update(id, title, introduce, cover, pics, status);
			} else{
				flag = pictureService.save(uid, title, introduce, null, pics, status);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			
			return null;
		}
		return "/admin/edit_pic";
	}
	
	/**
	 * 显示图库
	 */
	@Route("/admin/pic/:id")
	public String edit(Request request) {
		Integer id = request.pathParamToInt("id");
		// 编辑
		if (null != id) {
			Map<String, Object> picture = pictureService.getMap(null, id);
			request.attribute("picture", picture);
		}
		return "/admin/edit_pic";
	}
	
}
