package blade.fm.route.admin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.model.Mcat;
import blade.fm.route.BaseRoute;
import blade.fm.service.McatService;
import blade.fm.service.MusicService;
import blade.fm.service.SpecialService;
import blade.plugin.sql2o.Page;
import blade.render.ModelAndView;
import blade.servlet.Request;
import blade.servlet.Response;

import com.alibaba.fastjson.JSONObject;

/**
 * 音乐管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Path("/admin/music")
public class MusicRoute extends BaseRoute {

	@Inject
	private MusicService musicService;
	@Inject
	private McatService mcatService;
	@Inject
	private SpecialService specialService;

	/**
	 * 音乐列表
	 */
	@Route("/")
	public ModelAndView home(Request request) {
		return index(request);
	}
	
	/**
	 * 音乐列表
	 */
	@Route("/index")
	public ModelAndView index(Request request) {
		ModelAndView modelAndView = getAdminModelAndView("music");
		String singer = request.query("singer");
		String song = request.query("song");
		Integer uid = getUid(request);
		Integer page = request.queryToInt("page");
		
		Page<Map<String, Object>> musicPage = musicService.getPageMapList(uid, singer, song, null, null, 1, page,
				pageSize, "id desc");
		
		modelAndView.add("pageMap", musicPage);
		return modelAndView;
	}

	/**
	 * 显示音乐
	 */
	@Route("/:id")
	public ModelAndView edit_music(Request request) {
		ModelAndView modelAndView = getAdminModelAndView("edit_music");
		Integer mid = request.pathParamToInt("id");
		// 编辑
		if (null != mid) {
			Map<String, Object> music = musicService.getMap(null, mid);
			request.attribute("music", music);
		}
		List<Mcat> mcatList = mcatService.getList(1);
		List<Map<String, Object>> specialList = specialService.getList(null, 1, null, null, 1, "id desc");
		
		modelAndView.add("catList", mcatList);
		modelAndView.add("specialList", specialList);
		return modelAndView;
	}

	/**
	 * 保存音乐
	 */
	@Route("/save")
	public ModelAndView save(Request request, Response response) {
		ModelAndView modelAndView = getAdminModelAndView("edit_music");
		String step = request.query("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer mid = request.queryToInt("mid");
			String singer = request.query("singer");
			String song = request.query("song");
			Integer sid = request.queryToInt("sid");
			String song_path = request.query("song_path");
			String cover_path = request.query("cover_path");
			String introduce = request.query("introduce");
			String lrc = request.query("lrc");
			String cids = request.query("cids");

			boolean flag = false;
			Integer uid = getUid(request);
			
			if (null != mid) {
				flag = musicService.update(mid, singer, song, song_path, cover_path, introduce, cids, lrc, null, sid) > 0;
			} else {
				flag = musicService.save(uid, singer, song, song_path, cover_path, introduce, cids, lrc, null, sid);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", flag);
			response.json(jsonObject.toJSONString());
			return null;
		}
		
		List<Mcat> mcatList = mcatService.getList(1);
		List<Map<String, Object>> specialList = specialService.getList(null, 1, null, null, 1, "id desc");
		
		modelAndView.add("catList", mcatList);
		modelAndView.add("specialList", specialList);
		return modelAndView;
	}

	/**
	 * 删除音乐
	 */
	@Route("/del")
	public void del(Request request, Response response) {
		Integer mid = request.queryToInt("mid");
		boolean flag = false;
		if (null != mid) {
			flag = musicService.enable(mid, 0);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}

}
