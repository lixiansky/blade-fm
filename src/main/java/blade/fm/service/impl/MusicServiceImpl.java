package blade.fm.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import blade.fm.api.QiniuApi;
import blade.fm.cloud.model.Mcat;
import blade.fm.cloud.model.Music;
import blade.fm.cloud.model.User;
import blade.fm.service.FileService;
import blade.fm.service.McatService;
import blade.fm.service.MusicService;
import blade.fm.service.UserService;
import blade.fm.util.BeanUtil;
import blade.fm.util.WebConst;

import org.apache.log4j.Logger;
import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.DateUtil;
import org.unique.common.tools.FileUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.ioc.annotation.Service;
import org.unique.plugin.dao.Page;
import org.unique.plugin.dao.SqlBase;
import org.unique.plugin.db.exception.UpdateException;

import com.qiniu.api.rs.Entry;

@Service
public class MusicServiceImpl implements MusicService {

	private Logger logger = Logger.getLogger(MusicServiceImpl.class);

	@Autowired
	private FileService fileService;
	@Autowired
	private UserService userService;
	@Autowired
	private McatService mcatService;

	@Override
	public Music get(Integer mid) {
		return Music.db.findByPK(mid);
	}

	@Override
	public boolean save(Integer uid, String singer, String song, String song_path, String cover_path, String introduce,
			String cids, String lrc, String tags, Integer sid) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		
		String song_key = "", cover_key = "";
		if (StringUtils.isNotBlank(song_path)) {
			song_key = song_path;
			String filePath = WebConst.getWebRootPath() + song_path;
			if (!song_path.startsWith("http://") && FileUtil.isFile(filePath)) {
				//上传音乐
				fileService.upload(song_key, filePath);
			}
		}

		if (StringUtils.isNotBlank(cover_path)) {
			cover_key = cover_path;
			String filePath = WebConst.getWebRootPath() + cover_path;
			if (!cover_path.startsWith("http://") && FileUtil.isFile(filePath)) {
				//上传封面
				fileService.upload(cover_key, filePath);
			}
		}
		//2 保存数据库
		try {
			count = Music.db.insert(
					"insert into t_music(uid, singer, song, song_path, cover_path, introduce, cids, lrc, tags, sid, create_time) "
							+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", uid, singer, song, song_key, cover_key,
					introduce, cids, lrc, tags, sid, DateUtil.getCurrentTime());
		} catch (UpdateException e) {
			logger.warn("添加音乐失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;

	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String singer, String song, String tag, Integer sid,
			String order) {
		SqlBase base = SqlBase.select("select t.* from t_music t");
		base.eq("uid", uid).likeLeft("singer", singer).likeLeft("song", song).eq("sid", sid).like("tags", tag)
				.order(order);
		List<Music> list = Music.db.findList(base.getSQL(), base.getParams());
		return this.getMusicMapList(list);
	}

	@Override
	public Page<Music> getPageList(Integer uid, String singer, String song, String tag, Integer sid, Integer status,
			Integer page, Integer pageSize, String order) {
		SqlBase base = SqlBase.select("select t.* from t_music t");
		base.eq("t.uid", uid).likeLeft("t.singer", singer).like("t.song", song).eq("t.status", status).eq("t.sid", sid)
				.like("t.tags", tag).order(order);
		return Music.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}

	@Override
	public int delete(Integer id) {
		int count = 0;
		if (null != id) {
			Music music = this.get(id);
			if (null != music) {
				String key = music.getSong_path();
				if (StringUtils.isNotBlank(key) && !key.startsWith("http://")) {
					Entry musicEntry = fileService.getInfo(key);
					if (musicEntry.getStatusCode() == 200) {
						userService.updateUseSize(music.getUid(), -musicEntry.getFsize());
					}
				}
				String cover_key = music.getCover_path();
				if (StringUtils.isNotBlank(cover_key) && !cover_key.startsWith("http://")) {
					Entry coverEntry = fileService.getInfo(cover_key);
					if (coverEntry.getStatusCode() == 200) {
						userService.updateUseSize(music.getUid(), -coverEntry.getFsize());
					}
				}
				try {
					count = Music.db.delete("delete from t_music where id = ?", id);
				} catch (UpdateException e) {
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
				Music.db.delete("delete from t_music where id in (?)", ids);
			} catch (UpdateException e) {
				logger.warn("删除音乐失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int update(Integer id, String singer, String song, String song_path, String cover_path, String introduce,
			String cids, String lrc, String tags, Integer sid) {
		int count = 0;
		if (null != id) {

			Music music = this.get(id);
			if (null != music) {
				
				SqlBase base = SqlBase.update("update t_music");
				//判断是否修改歌名
				if (StringUtils.isNotBlank(song) && !song.equals(music.getSong())) {
					base.set("song", song);
				}
				//判断是否修改歌手
				if (StringUtils.isNotBlank(singer) && !singer.equals(music.getSinger())) {
					base.set("singer", singer);
				}
				
				String song_key = "", cover_key = "";
				//判断音乐文件是否修改
				if (StringUtils.isNotBlank(song_path) && !song_path.equals(music.getSong_path())) {
					song_key = song_path;
					String filePath = WebConst.getWebRootPath() + song_path;
					if (!song_path.startsWith("http://") && FileUtil.isFile(filePath)) {
						//上传封面
						fileService.upload(song_key, filePath);
						//删除原有文件
						fileService.delete(music.getSong_path());
					}
					base.set("song_path", song_key);
				}
				//判断音乐封面是否修改
				if (StringUtils.isNotBlank(cover_path) && !cover_path.equals(music.getCover_path())) {
					cover_key = cover_path;
					String filePath = WebConst.getWebRootPath() + cover_path;
					if (!cover_path.startsWith("http://") && FileUtil.isFile(filePath)) {
						//上传封面
						fileService.upload(cover_key, filePath);
						//删除原有文件
						fileService.delete(music.getCover_path());
					}
					base.set("cover_path", cover_key);
				}
				
				//是否修改描述
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(music.getIntroduce())) {
					base.set("introduce", introduce);
				}
				//是否修改分类
				if (StringUtils.isNotBlank(cids) && !cids.equals(music.getCids())) {
					base.set("cids", cids);
				}
				//是否修改tags
				if (StringUtils.isNotBlank(tags) && !tags.equals(music.getTags())) {
					base.set("tags", tags);
				}
				//是否修改歌词信息
				if (StringUtils.isNotBlank(lrc) && !lrc.equals(music.getLrc())) {
					base.set("lrc", lrc);
				}
				base.eq("id", music.getId());
				try {
					if (base.getSetMap().size() == 0) {
						return 1;
					}
					count = Music.db.update(base.getSQL(), base.getParams());
				} catch (UpdateException e) {
					logger.warn("更新音乐失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> getRandom(Integer count) {
		List<Music> list = Music.db.findList(
				"select t1.id,t1.singer,t1.song,t1.song_path,t1.like_count from t_music t1 "
						+ "join(select max(id) id from  t_music) t2 " + "on (t1.id >= floor( t2.id*rand() )) limit ?",
				count);
		return this.getMusicMapList(list);
	}

	/**
	 * 私有的list转map
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> getMusicMapList(List<Music> list) {
		List<Map<String, Object>> mapList = CollectionUtil.newArrayList();
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
				Music.db.update("update t_music set like_count=(like_count + 1) where id = ?", id);
				break;
			case 2:
				Music.db.update("update t_music set download_count=(download_count + 1) where id = ?", id);
				break;
			}
		}
	}

	@Override
	public Map<String, Object> getMap(Music music, Integer mid) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == music) {
			music = this.get(mid);
		}
		if (null != music) {
			resultMap = BeanUtil.toMap(music);
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
				List<String> mcatList = CollectionUtil.newArrayList();
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
				resultMap.put("date_zh", DateUtil.convertIntToDatePattern(music.getCreate_time(), "yyyy/MM/dd"));
				resultMap.put("time_zh",
						DateUtil.convertIntToDatePattern(music.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
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
			return Music.db.update("update t_music set status = ? where id = ?", status, mid) > 0;
		}
		return false;
	}

}
