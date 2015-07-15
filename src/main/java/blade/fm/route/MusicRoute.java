package blade.fm.route;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.Constant;
import blade.fm.route.BaseRoute;
import blade.fm.service.MusicService;
import blade.plugin.sql2o.Page;
import blade.render.ModelAndView;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 前台音乐展示
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
@Path("/music")
public class MusicRoute extends BaseRoute {
	
	@Inject
	private MusicService musicService;
	
	@Route("/")
	public ModelAndView home(Request request, Response response){
		ModelAndView modelAndView = new ModelAndView("music");
		String singer = request.query("singer");
		String song = request.query("song");
		Integer uid = getUid(request);
		Integer page = request.queryToInt("page");
		
		Page<Map<String, Object>> pageData = musicService.getPageMapList(uid, singer, song, null, null, 1, page, pageSize, "create_time desc");
		
		modelAndView.add("pageEntity", pageData);
		
		return modelAndView; 
	}
	
	/**
	 * 最新10首歌
	 */
	@Route("new_music")
	public void new_music(Request request, Response response){
		Integer page = request.queryToInt("page");
		Page<Map<String, Object>> top10List = musicService.getPageMapList(null, null, null, null, null, 1, page, 9, "create_time desc");
		response.json(JSON.toJSONString(top10List));
	}
	
	/**
	 * 最热门的歌
	 */
	@Route("hot")
	public void hot(Request request, Response response){
		Integer page = request.queryToInt("page");
		Page<Map<String, Object>> hotList = musicService.getPageMapList(null, null, null, null, null, 1, page, 9, "like_count desc");
		response.json(JSON.toJSONString(hotList));
	}
	
	/**
	 * 按歌名查询
	 */
	@Route("search")
	public void search(Request request, Response response){
		String song = request.query("song");
		String singer = request.query("singer");
		Page<Map<String, Object>> top10List = musicService.getPageMapList(null, singer, song, null, null, 1, 1, 10, "like_count desc");
		response.json(JSON.toJSONString(top10List));
	}
	
	/**
	 * 随机推荐
	 */
	@Route("random")
	public void random(Request request, Response response){
		List<Map<String, Object>> randomList = musicService.getRandom(10);
		response.json(JSON.toJSONString(randomList));
	}
	
	/**
	 * 删除一首音乐
	 */
	@Route("delete_music")
	public void delete_music(Request request, Response response){
		Integer mid = request.queryToInt("mid");
		int count = musicService.delete(mid);
		boolean flag = count > 0;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", flag);
		response.json(jsonObject.toJSONString());
	}
	
	/**
	 * 1喜欢/2收听/3下载音乐 点击+1
	 */
	@Route("hit")
	public void hit(Request request, Response response){
		Integer mid = request.queryToInt("mid");
		Integer type = request.queryToInt("type");
		musicService.like(mid, type);
		response.text(Constant.MSG_SUCCESS);
	}
	
	/**
	 * 查询音乐信息
	 */
	@Route("get_music")
	public void getMusic(Request request, Response response){
		Integer mid = request.queryToInt("mid");
		if(null != mid){
			Map<String, Object> map = musicService.getMap(null, mid);
			response.json(JSON.toJSONString(map));
		}
	}
	
}
