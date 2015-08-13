package blade.fm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.Blade;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.fm.QiniuApi;
import blade.fm.model.Special;
import blade.fm.model.User;
import blade.fm.service.FileService;
import blade.fm.service.MusicService;
import blade.fm.service.RadioService;
import blade.fm.service.SpecialService;
import blade.fm.service.UserService;
import blade.fm.util.BeanUtil;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;

@Component
public class SpecialServiceImpl implements SpecialService {

	private Logger logger = Logger.getLogger(SpecialServiceImpl.class);

	@Inject
	private FileService fileService;
	@Inject
	private MusicService musicService;
	@Inject
	private RadioService radioService;
	@Inject
	private UserService userService;

	private Model<Special> model = new Model<Special>(Special.class);
	
	@Override
	public Special get(Integer sid) {
		return model.select().fetchByPk(sid);
	}

	@Override
	public Map<String, Object> getMap(Special special, Integer sid) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == special) {
			special = this.get(sid);
		}
		if (null != special) {
			resultMap = BeanUtil.toMap(special);
			
			// 封面
			if (StringUtils.isNotBlank(special.getCover())) {
				if (special.getCover().startsWith("http://")) {
					resultMap.put("cover_url", special.getCover());
				} else {
					String cover_url = QiniuApi.getUrlByKey(special.getCover());
					resultMap.put("cover_url", cover_url);
				}
			}
			// 创建时间
			if (null != special.getCreate_time()) {
				resultMap.put("date_zh",
						DateKit.formatDateByUnixTime(special.getCreate_time(), "yyyy-MM-dd"));
				resultMap.put("create_time_zh",
						DateKit.formatDateByUnixTime(special.getCreate_time(), "yyyy-MM-dd HH:mm"));
			}
			// 上传人
			if(null != special.getUid()){
				User user = userService.getByUid(special.getUid());
				resultMap.put("nickname", user.getNickname());
			}
			// 最后更新时间
			if (null != special.getLast_time()) {
				resultMap.put("last_time_zh",
						DateKit.formatDateByUnixTime(special.getLast_time(), "yyyy-MM-dd HH:mm"));
			}

		}
		return resultMap;
	}

	@Override
	public boolean save(Integer uid, String title, String introduce, String cover,
			Integer is_fine, Integer status) {
		int count = 0;
		Integer currentTime = DateKit.getUnixTimeByDate(new Date());
		uid = (null == uid) ? 1 : uid;
		
		String cover_key = "";
		if (StringUtils.isNotBlank(cover)) {
			cover_key = cover;
			String filePath = Blade.webRoot() + cover;
			if (!cover.startsWith("http://") && FileKit.isFile(filePath)) {
				//上传专辑封面
				fileService.upload(cover_key, filePath);
			}
		}
		try {
			count = model.insert()
					.param("uid", uid)
					.param("title", title)
					.param("introduce", introduce)
					.param("cover", cover_key)
					.param("create_time", currentTime)
					.param("last_time", currentTime)
					.param("is_fine", is_fine)
					.param("status", status).executeAndCommit();
		} catch (Exception e) {
			logger.warn("保存专辑失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public int update(Integer sid, Integer uid, String title, String introduce, String cover,
			Integer is_fine, Integer status) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		if (null != sid) {
			Special special = this.get(sid);
			if (null != special) {
				
				Model<Special> updateModel = model.update();
				
				if (StringUtils.isNotBlank(title) && !title.equals(special.getTitle())) {
					updateModel.param("title", title);
				}
				
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(special.getIntroduce())) {
					updateModel.param("introduce", introduce);
				}
				// 封面是否修改
				
				if (StringUtils.isNotBlank(cover)) {
					String cover_key = cover;
					String filePath = Blade.webRoot() + cover;
					if (!cover.startsWith("http://") && FileKit.isFile(filePath)) {
						//上传封面
						fileService.upload(cover_key, filePath);
						//删除原有文件
						fileService.delete(special.getCover());
					}
					updateModel.param("cover", cover_key);
				}
				if(null != is_fine){
					updateModel.param("is_fine", is_fine);
				}
				updateModel.param("last_time", DateKit.getUnixTimeByDate(new Date()));
				
				try {
					count = updateModel.where("sid", sid).executeAndCommit();
				} catch (Exception e) {
					logger.warn("更新专辑失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, Integer type, Integer is_fine, String title, Integer status, String order) {
		List<Special> list = 
				model.select().where("uid", uid).where("type", type)
				.where("is_fine", is_fine).like("title", "%" + title)
				.where("status", status).orderBy(order).fetchList();
		return this.getSpecialMapList(list);
	}

	private List<Map<String, Object>> getSpecialMapList(List<Special> list) {
		List<Map<String, Object>> mapList = CollectionKit.newArrayList();
		if(null == list || list.size() == 0){
			return mapList;
		}
		for (int i = 0, len = list.size(); i < len; i++) {
			Special special = list.get(i);
			if (null != special) {
				mapList.add(this.getMap(special, null));
			}
		}
		return mapList;
	}

	private Page<Special> getPageList(Integer uid, Integer type, Integer is_fine,
			String title, Integer status, Integer page,
			Integer pageSize, String order) {
		return model.select().where("uid", uid).where("type", type)
				.where("is_fine", is_fine).like("title", "%" + title)
				.where("status", status).orderBy(order).fetchPage(page, pageSize);
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, Integer type, Integer is_fine,
			String title, Integer status,
			Integer page, Integer pageSize, String order) {
		Page<Special> pageList = this.getPageList(uid, type, is_fine, title, status, page, pageSize, order);

		List<Special> specialList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getSpecialMapList(specialList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public int delete(Integer sid) {
		int count = 0;
		if (null != sid) {
			try {
				count = model.update().param("status", 0).where("id", sid).executeAndCommit();
			} catch (Exception e) {
				logger.warn("删除专辑失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int hit(Integer sid) {
		if (null != sid) {
			return model.update().param("hit", "(hit+1)").where("id", sid).executeAndCommit();
		}
		return 0;
	}

	@Override
	public boolean enable(Integer sid, Integer status) {
		if (null != sid) {
			try {
				return model.update().param("status", status).where("id", sid).executeAndCommit() > 0;
			} catch (Exception e) {
				logger.warn("停用启用专辑失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

}
