package blade.fm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.Blade;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.fm.QiniuApi;
import blade.fm.model.Radio;
import blade.fm.model.Special;
import blade.fm.model.User;
import blade.fm.service.FileService;
import blade.fm.service.RadioService;
import blade.fm.service.SpecialService;
import blade.fm.service.UserService;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;

@Component
public class RadioServiceImpl implements RadioService {

	private Logger logger = Logger.getLogger(RadioServiceImpl.class);

	@Inject
	private FileService fileService;
	@Inject
	private SpecialService specialService;
	@Inject
	private UserService userService;

	private Radio model = new Radio();
	
	@Override
	public Radio get(Integer id) {
		return model.select().fetchByPk(id);
	}

	@Override
	public Map<String, Object> getMap(Radio radio, Integer id) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == radio) {
			radio = this.get(id);
		}
		if (null != radio) {
			resultMap.put("id", radio.getId());
			resultMap.put("uid", radio.getUid());
			resultMap.put("sid", radio.getSid());
			resultMap.put("title", radio.getTitle());
			resultMap.put("status", radio.getStatus());
			
			// 歌曲路径
			if (StringUtils.isNotBlank(radio.getUrl())) {
				if (radio.getUrl().startsWith("http://")) {
					resultMap.put("radio_url", radio.getUrl());
				} else {
					String url = QiniuApi.getUrlByKey(radio.getUrl());
					resultMap.put("radio_url", url);
				}
			}
			// 上传日期
			if (null != radio.getCreate_time()) {
				resultMap.put("date_zh", DateKit.formatDateByUnixTime(radio.getCreate_time(), "yyyy/MM/dd"));
				resultMap.put("time_zh",
						DateKit.formatDateByUnixTime(radio.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			}
			// 上传人
			if(null != radio.getUid()){
				User user = userService.getByUid(radio.getUid());
				resultMap.put("nickname", user.getNickname());
			}
			// 所属专辑
			if(null != radio.getSid()){
				Special special = specialService.get(radio.getSid());
				resultMap.put("special", special.getTitle());
			}
		}
		return resultMap;
	}

	@Override
	public boolean save(Integer uid, String title, Integer sid, String url) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		String url_key = "";
		if (StringUtils.isNotBlank(url)) {
			url_key = url;
			String filePath = Blade.webRoot() + url;
			if (!url.startsWith("http://") && FileKit.isFile(filePath)) {
				//上传radio
				fileService.upload(url_key, filePath);
			}
		}
		try {
			count = model.insert()
					.param("uid", uid)
					.param("title", title)
					.param("sid", sid)
					.param("url", url_key)
					.param("create_time", DateKit.getUnixTimeByDate(new Date()))
					.param("status", 1).executeAndCommit();
					
		} catch (Exception e) {
			logger.warn("添加电台失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public boolean update(Integer id, String title, Integer sid, String url) {
		int count = 0;
		if (null != id) {
			Radio radio = this.get(id);
			String url_key = "";
			
			Model updateModel = model.update();
			
			//判断音乐文件是否修改
			if (StringUtils.isNotBlank(url)) {
				url_key = url;
				String filePath = Blade.webRoot() + url;
				if (!url.startsWith("http://") && FileKit.isFile(filePath)) {
					//上传电台文件
					fileService.upload(url_key, filePath);
					//删除原有文件
					fileService.delete(radio.getUrl());
				}
				updateModel.param("url", url_key);
			}
			
			if (StringUtils.isNotBlank(title)) {
				updateModel.param("title", title);
			}
			if (null != sid) {
				updateModel.param("sid", sid);
			}
			
			try {
				count = updateModel.where("id", radio.getId()).executeAndCommit();
			} catch (Exception e) {
				logger.warn("更新电台失败：" + e.getMessage());
				count = 0;
			}
		}
		return count > 0;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String title, Integer sid, Integer status, String order) {
		List<Radio> list = model.select().where("uid", uid).like("title", "%" + title)
				.where("status", status).where("sid", sid).orderBy(order).fetchList();
		return this.getRadioMapList(list);
	}

	private List<Map<String, Object>> getRadioMapList(List<Radio> list) {
		List<Map<String, Object>> mapList = CollectionKit.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Radio radio = list.get(i);
			if (null != radio) {
				Map<String, Object> map = this.getMap(radio, null);
				mapList.add(map);
			}
		}
		return mapList;
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer sid, Integer status,
			Integer page, Integer pageSize, String order) {
		Page<Radio> pageList = this.getPageList(uid, title, sid, status, page, pageSize, order);

		List<Radio> musicList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getRadioMapList(musicList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	private Page<Radio> getPageList(Integer uid, String title, Integer sid, Integer status, Integer page,
			Integer pageSize, String order) {
		return model.select().where("uid", uid).like("title", "%" + title)
				.where("status", status).where("sid", sid).orderBy(order).fetchPage(page, pageSize);
	}
	
	@Override
	public boolean delete(Integer id) {
		if (null != id) {
			Radio radio = this.get(id);
			if(null != radio){
				if(null != radio.getUrl() && !radio.getUrl().startsWith("http://")){
					fileService.delete(radio.getUrl());
				}
				
				return model.delete().where("id", id).executeAndCommit() > 0;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean enable(Integer id, Integer status) {
		if (null != id) {
			return model.update().param("status", status).where("id", id).executeAndCommit() > 0;
		}
		return false;
	}

}
