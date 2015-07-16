package blade.fm.route;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.AlbumService;
import blade.plugin.sql2o.Page;
import blade.render.ModelAndView;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 相册/图片管理
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Path("/pictures")
public class PictureRoute extends BaseRoute {

	@Inject
	private AlbumService pictureService;
	
	@Route("/")
	public ModelAndView index(Request request, Response response){
		ModelAndView modelAndView = new ModelAndView("picture");
		String mode = request.query("mode");
		Integer uid = getUid(request);
		Integer page = request.queryToInt("page");
		page = (null == page || page < 1) ? 1 : page;
		
		Page<Map<String, Object>> picPage = pictureService.getPageMapList(uid, null, 1, page, pageSize, "create_time desc");
		if(StringUtils.isNotBlank(mode) && mode.equals("ajax")){
			response.json(JSON.toJSONString(picPage));
			return null;
		} else{
			modelAndView.add("picPage", picPage);
		}
		return modelAndView;
	}
	
	/**
	 * 相册详情
	 */
	@Route("/:id")
	public ModelAndView detail(Request request){
		ModelAndView modelAndView = new ModelAndView("preview");
		Integer id = request.pathParamToInt("id");
		if(null != id){
			Map<String, Object> pictrue = pictureService.getMap(null, id);
			modelAndView.add("pictrue", pictrue);
		}
		return modelAndView;
	}
}
