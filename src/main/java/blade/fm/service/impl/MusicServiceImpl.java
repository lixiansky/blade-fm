package blade.fm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.Blade;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.fm.QiniuApi;
import blade.fm.model.Mcat;
import blade.fm.model.Music;
import blade.fm.model.User;
import blade.fm.service.FileService;
import blade.fm.service.McatService;
import blade.fm.service.MusicService;
import blade.fm.service.UserService;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;

@Component
public class MusicServiceImpl implements MusicService {

	private Logger logger = Logger.getLogger(MusicServiceImpl.class);

	private Model<Music> model = new Model<Music>(Music.class);
	
	@Inject
	private FileService fileService;
	@Inject
	private UserService userService;
	@Inject
	private McatService mcatService;

	@Override
	public Music get(Integer mid) {
		return model.select().fetchByPk(mid);
	}

	@Override
	public boolean save(Integer uid, String singer, String song, String song_path, String cover_path, String introduce,
			String cids, String lrc, String tags, Integer sid) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		
		String song_key = "", cover_key = "";
		if (StringKit.isNotBlank(song_path)) {
			song_key = song_path;
			String filePath = Blade.webRoot() + "/" + song_path;
			if (!song_path.startsWith("http://") && FileKit.isFile(filePath)) {
				//上传音乐
				fileService.upload(song_key, filePath);
			}
		}

		if (StringKit.isNotBlank(cover_path)) {
			cover_key = cover_path;
			String filePath = Blade.webRoot() + "/" + cover_path;
			if (!cover_path.startsWith("http://") && FileKit.isFile(filePath)) {
				//上传封面
				fileService.upload(cover_key, filePath);
			}
		}
		//2 保存数据库
		try {
			count = model.insert()
					.param("uid", uid)
					.param("singer", singer)
					.param("song", song)
					.param("song_path", song_key)
					.param("cover_path", cover_key)
					.param("introduce", introduce)
					.param("cids", cids)
					.param("lrc", lrc)
					.param("tags", tags)
					.param("sid", sid)
					.param("create_time", DateKit.getUnixTimeByDate(new Date())).executeAndCommit(Integer.class);
		} catch (Exception e) {
			logger.warn("添加音乐失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;

	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String singer, String song, String tag, Integer sid,
			String order) {
		
		List<Music> list = model.select()
				.where("uid", uid)
				.like("singer", singer)
				.like("song", song)
				.where("sid", sid)
				.like("tags","%" + tag + "%")
				.orderBy(order).fetchList();
		
		return this.getMusicMapList(list);
	}

	@Override
	public Page<Music> getPageList(Integer uid, String singer, String song, String tag, Integer sid, Integer status,
			Integer page, Integer pageSize, String order) {

		return model.select()
				.where("uid", uid)
				.like("singer", singer)
				.like("song", song)
				.where("status", status)
				.where("sid", sid)
				.like("tags","%" + tag + "%")
				.orderBy(order).fetchPage(page, pageSize);
	}

	@Override
	public int delete(Integer id) {
		int count = 0;
		if (null != id) {
			Music music = this.get(id);
			if (null != music) {
				String key = music.getSong_path();
				if (StringUtils.isNotBlank(key) && !key.startsWith("http://")) {
//					Entry musicEntry = fileService.getInfo(key);
//					if (musicEntry.getStatusCode() == 200) {
//						userService.updateUseSize(music.getUid(), -musicEntry.getFsize());
//					}
				}
				String cover_key = music.getCover_path();
				if (StringUtils.isNotBlank(cover_key) && !cover_key.startsWith("http://")) {
//					Entry coverEntry = fileService.getInfo(cover_key);
//					if (coverEntry.getStatusCode() == 200) {
//						userService.updateUseSize(music.getUid(), -coverEntry.getFsize());
//					}
				}
				try {
					count = model.delete().where("id", id).executeAndCommit(Integer.class);
				} catch (Exception e) {
					logger.warn("删除音乐失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public int delete(String ids) {
		int count = 0;
		if (null != ids) {
			try {
				count = model.delete().in("id", ids.split(",")).executeAndCommit(Integer.class);
			} catch (Exception e) {
				logger.warn("删除音乐失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public boolean update(Integer id, String singer, String song, String song_path, String cover_path, String introduce,
			String cids, String lrc, String tags, Integer sid) {
		int count = 0;
		if (null != id) {

			Music music = this.get(id);
			if (null != music) {
				
				Model<Music> updateModel = model.update();
				
				//判断是否修改歌名
				if (StringUtils.isNotBlank(song) && !song.equals(music.getSong())) {
					updateModel.param("song", song);
				}
				//判断是否修改歌手
				if (StringUtils.isNotBlank(singer) && !singer.equals(music.getSinger())) {
					updateModel.param("singer", singer);
				}
				
				String song_key = "", cover_key = "";
				//判断音乐文件是否修改
				if (StringUtils.isNotBlank(song_path) && !song_path.equals(music.getSong_path())) {
					song_key = song_path;
					String filePath = Blade.webRoot() + "/" + song_path;
					if (!song_path.startsWith("http://") && FileKit.isFile(filePath)) {
						//上传封面
						fileService.upload(song_key, filePath);
						//删除原有文件
						fileService.delete(music.getSong_path());
						
						updateModel.param("song_path", song_key);
						
					}
				}
				
				//判断音乐封面是否修改
				if (StringUtils.isNotBlank(cover_path) && !cover_path.equals(music.getCover_path())) {
					cover_key = cover_path;
					String filePath = Blade.webRoot() + "/" + song_path;
					if (!cover_path.startsWith("http://") && FileKit.isFile(filePath)) {
						//上传封面
						fileService.upload(cover_key, filePath);
						//删除原有文件
						fileService.delete(music.getCover_path());
						
						updateModel.param("cover_path", cover_key);
					}
				}
				
				//是否修改描述
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(music.getIntroduce())) {
					updateModel.param("introduce", introduce);
				}
				
				//是否修改分类
				if (StringUtils.isNotBlank(cids) && !cids.equals(music.getCids())) {
					updateModel.param("cids", cids);
				}
				
				//是否修改tags
				if (StringUtils.isNotBlank(tags) && !tags.equals(music.getTags())) {
					updateModel.param("tags", tags);
				}
				//是否修改歌词信息
				if (StringUtils.isNotBlank(lrc) && !lrc.equals(music.getLrc())) {
					updateModel.param("lrc", lrc);
				}
				
				try {
					count = updateModel.where("id", music.getId()).executeAndCommit();
				} catch (Exception e) {
					logger.warn("更新音乐失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count > 0;
	}

	@Override
	public List<Map<String, Object>> getRandom(Integer count) {
		List<Music> list = model.select("select t1.id,t1.singer,t1.song,t1.song_path,t1.like_count from t_music t1 "
				+ "join(select max(id) id from  t_music) t2 " + "on (t1.id >= floor( t2.id*rand() )) limit " + count).fetchList();
		return this.getMusicMapList(list);
	}

	/**
	 * 私有的list转map
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> getMusicMapList(List<Music> list) {
		List<Map<String, Object>> mapList = CollectionKit.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Music music = list.get(i);
			if (null != music) {
				Map<String, Object> map = this.getMap(music, null);
				map.remove("lrc");
				mapList.add(map);
			}
		}
		return mapList;
	}

	@Override
	public void like(Integer id, Integer type) {
		if (null != id && null != type) {
			switch (type) {
			case 1:
				model.update().param("like_count", "(like_count + 1)").where("id", id).executeAndCommit();
				break;
			case 2:
				model.update().param("download_count", "(download_count + 1)").where("id", id).executeAndCommit();
				break;
			}
		}
	}

	@Override
	public Map<String, Object> getMap(Music music, Integer mid) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == music) {
			music = this.get(mid);
		}
		if (null != music) {
			resultMap.put("id", music.getId());
			resultMap.put("singer", music.getSinger());
			resultMap.put("song", music.getSong());
			resultMap.put("introduce", music.getIntroduce());
			resultMap.put("lrc", music.getLrc());
			resultMap.put("like_count", music.getLike_count());
			resultMap.put("download_count", music.getDownload_count());
			resultMap.put("status", music.getStatus());
			// 歌曲路径
			if (StringUtils.isNotBlank(music.getSong_path())) {
				if (music.getSong_path().startsWith("http://")) {
					resultMap.put("mp3_url", music.getSong_path());
				} else {
					String music_url = QiniuApi.getUrlByKey(music.getSong_path());
					resultMap.put("mp3_url", music_url);
				}
			}
			// 歌曲封面
			if (StringUtils.isNotBlank(music.getCover_path())) {
				if (music.getCover_path().startsWith("http://")) {
					resultMap.put("cover_url", music.getCover_path());
				} else {
					String cover_url = QiniuApi.getUrlByKey(music.getCover_path());
					resultMap.put("cover_url", cover_url);
				}
			}
			// 所属分类
			if (StringUtils.isNotBlank(music.getCids())) {
				String[] cids = music.getCids().split(",");
				List<String> mcatList = CollectionKit.newArrayList();
				if (null != cids && cids.length > 0) {
					for (String cid : cids) {
						Mcat mcat = mcatService.get(Integer.valueOf(cid));
						if (null != mcat) {
							mcatList.add(mcat.getName());
						}
					}
				}
				resultMap.put("mcat", mcatList.toString());
				resultMap.put("mcatList", Arrays.asList(cids));
			}
			// 上传日期
			if (null != music.getCreate_time()) {
				resultMap.put("date_zh", DateKit.formatDateByUnixTime(music.getCreate_time(), "yyyy/MM/dd"));
				resultMap.put("time_zh",
						DateKit.formatDateByUnixTime(music.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			}
			// 上传人
			if (null != music.getUid()) {
				User user = userService.getByUid(music.getUid());
				if(null != user){
					resultMap.put("nickname", user.getNickname());
				}
			}
		}
		return resultMap;
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, String singer, String song, String tag, Integer sid,
			Integer status, Integer page, Integer pageSize, String order) {
		Page<Music> pageList = this.getPageList(uid, singer, song, tag, sid, status, page, pageSize, order);

		List<Music> musicList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount() , pageList.getPage(), pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getMusicMapList(musicList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public boolean enable(Integer mid, Integer status) {
		if (null != mid) {
			return model.update().param("status", status).where("id", mid).executeAndCommit(Integer.class) > 0;
		}
		return false;
	}

}
