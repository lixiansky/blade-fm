package blade.fm.route;

import java.util.List;
import java.util.Map;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.service.FocusService;
import blade.fm.service.MusicService;
import blade.fm.service.RadioService;
import blade.fm.service.SpecialService;
import blade.fm.service.UserService;
import blade.plugin.sql2o.Page;
import blade.render.ModelAndView;
import blade.servlet.Request;

@Path("/special")
public class SpecialRoute extends BaseRoute {

	@Inject
	private UserService userService;
	@Inject
	private MusicService musicService;
	@Inject
	private SpecialService specialService;
	@Inject
	private FocusService focusService;
	@Inject
	private RadioService radioService;
	
	@Route("/")
	public ModelAndView index(Request request){
		
		ModelAndView modelAndView = new ModelAndView("special");
		
		Integer page = request.queryToInt("page");
		page = (null == page || page < 1) ? 1 : page;
		
		//焦点图
		List<Map<String, Object>> focusList = focusService.getList(1, null, 1, "create_time desc");
		
		//精选电台
		Page<Map<String, Object>> finePage = specialService.getPageMapList(null, 2, 1, null, 1, page, pageSize, "last_time desc");
		
		//热门电台
		Page<Map<String, Object>> hotPage = specialService.getPageMapList(null, 2, null, null, 1, page, pageSize, "hit desc");
		modelAndView.add("focusList", focusList);
		modelAndView.add("finePage", finePage);
		modelAndView.add("hotPage", hotPage);
		
		return modelAndView;
	}
	
	/**
	 * 电台列表
	 */
	@Route("/:sid")
	public ModelAndView special_content(Request request) {
		ModelAndView modelAndView = new ModelAndView("radio");
		Integer sid = request.pathParamToInt("sid");
		if(null != sid){
			// 专辑信息
			Map<String, Object> specialMap = specialService.getMap(null, sid);
			// 电台列表
			List<Map<String, Object>> radioList = radioService.getList(null, null, sid, 1, "create_time desc");
			modelAndView.add("specialMap", specialMap);
			modelAndView.add("radioList", radioList);
		}
		return modelAndView;
	}
	
}
