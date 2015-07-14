package blade.fm.route.admin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.model.Mcat;
import blade.fm.route.BaseRoute;
import blade.fm.service.McatService;
import blade.fm.service.MusicService;
import blade.fm.service.SpecialService;
import blade.plugin.sql2o.Page;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 音乐管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Path
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
	@Route("/admin/music/index")
	public String index(Request request) {
		String singer = request.query("singer");
		String song = request.query("song");
		Page<Map<String, Object>> musicPage = musicService.getPageMapList(uid, singer, song, null, null, 1, page,
				pageSize, "id desc");
		request.attribute("pageMap", musicPage);
		return "/admin/music";
	}

	/**
	 * 显示音乐
	 */
	@Route("/admin/music/:id")
	public String edit_music(Request request) {
		Integer mid = request.pathParamToInt("id");
		// 编辑
		if (null != mid) {
			Map<String, Object> music = musicService.getMap(null, mid);
			request.attribute("music", music);
		}
		List<Mcat> mcatList = mcatService.getList(1);
		List<Map<String, Object>> specialList = specialService.getList(null, 1, null, null, 1, "id desc");
		request.attribute("catList", mcatList);
		request.attribute("specialList", specialList);
		return "/admin/edit_music";
	}

	/**
	 * 保存音乐
	 */
	@Route("/admin/music/save")
	public String save(Request request, Response response) {
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
			uid = 1;
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
		request.attribute("catList", mcatList);
		request.attribute("specialList", specialList);
		return "/admin/edit_music";
	}

	/**
	 * 删除音乐
	 */
	@Route("/admin/music/del")
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
