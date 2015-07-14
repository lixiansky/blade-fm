package blade.fm.route.front;

import java.util.List;
import java.util.Map;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.FocusService;
import blade.fm.service.MusicService;
import blade.fm.service.OpenService;
import blade.fm.service.RadioService;
import blade.fm.service.SpecialService;
import blade.fm.service.UserService;
import blade.plugin.sql2o.Page;
import blade.servlet.Request;

@Path("/special")
public class SpecialController extends BaseRoute {

	@Inject
	private UserService userService;
	@Inject
	private MusicService musicService;
	@Inject
	private SpecialService specialService;
	@Inject
	private OpenService openService;
	@Inject
	private FocusService focusService;
	@Inject
	private RadioService radioService;
	
	@Route("index")
	public String index(Request request){
		//焦点图
		List<Map<String, Object>> focusList = focusService.getList(1, null, 1, "create_time desc");
		//精选电台
		Page<Map<String, Object>> finePage = specialService.getPageMapList(null, 2, 1, null, 1, page, pageSize, "last_time desc");
		//热门电台
		Page<Map<String, Object>> hotPage = specialService.getPageMapList(null, 2, null, null, 1, page, pageSize, "hit desc");
		request.attribute("focusList", focusList);
		request.attribute("finePage", finePage);
		request.attribute("hotPage", hotPage);
		return "special";
	}
	
	/**
	 * 电台列表
	 */
	@Route("/special/:sid")
	public String special_content(Request request) {
		Integer sid = request.pathParamToInt("sid");
		if(null != sid){
			// 专辑信息
			Map<String, Object> specialMap = specialService.getMap(null, sid);
			// 电台列表
			List<Map<String, Object>> radioList = radioService.getList(null, null, sid, 1, "create_time desc");
			request.attribute("specialMap", specialMap);
			request.attribute("radioList", radioList);
		}
		return "radio";
	}
	
}
