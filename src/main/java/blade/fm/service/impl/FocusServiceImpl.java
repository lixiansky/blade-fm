package blade.fm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Component;
import blade.annotation.Inject;
import blade.fm.Constant;
import blade.fm.QiniuApi;
import blade.fm.model.Focus;
import blade.fm.service.FileService;
import blade.fm.service.FocusService;
import blade.fm.util.BeanUtil;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Page;

@Component
public class FocusServiceImpl implements FocusService {

	private Logger logger = Logger.getLogger(FocusServiceImpl.class);
	private Focus model = new Focus();
	@Inject
	private FileService fileService;
	
	@Override
	public Focus get(Integer id) {
		return model.fetchByPk(id);
	}

	@Override
	public Map<String, Object> getMap(Focus focus, Integer id) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == focus) {
			focus = this.get(id);
		}
		if (null != focus) {
			resultMap = BeanUtil.toMap(focus);
			// 焦点图
			if (StringUtils.isNotBlank(focus.getPic())) {
				if (focus.getPic().startsWith("http://")) {
					resultMap.put("pic_url", focus.getPic());
				} else {
					String cover_url = QiniuApi.getUrlByKey(focus.getPic());
					resultMap.put("pic_url", cover_url);
				}
			}
			if(null != focus.getCreate_time()){
				resultMap.put("date_zh", DateKit.formatDateByUnixTime(focus.getCreate_time(), "yyyy/MM/dd"));
			}
		}
		return resultMap;
	}

	@Override
	public boolean save(String title, String introduce, String pic, Integer type) {
		int count = 0;
		String key = "";
		if (StringUtils.isNotBlank(pic)) {
			key = pic;
			String filePath = Constant.WEB_ROOT + "/" + pic;
			if (!pic.startsWith("http://") && FileKit.isFile(filePath)) {
				//上传音乐
				fileService.upload(key, filePath);
			}
		}
		try {
			Integer create_time = DateKit.getUnixTimeByDate(new Date());
			count = model.insert()
			.param("title", title)
			.param("introduce", introduce)
			.param("pic", pic)
			.param("type", type)
			.param("create_time", create_time)
			.param("status", 1).executeAndCommit(Integer.class);
		} catch (Exception e) {
			logger.warn("保存焦点图失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public int update(Integer id, String title, String introduce, String pic, Integer type, Integer status) {
		int count = 0;
		if (null != id) {
			Focus focus = this.get(id);
			if (null != focus) {
				
				
				String titleStr = null;
				String introduceStr = null;
				String pic_key = null;
				
				if (StringUtils.isNotBlank(title) && !title.equals(focus.getTitle())) {
					titleStr = title;
				}
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(focus.getIntroduce())) {
					introduceStr = introduce;
				}
				// 幻灯片是否修改
				if (StringUtils.isNotBlank(pic)) {
					pic_key = pic;
					String filePath = Constant.WEB_ROOT + "/" + pic;
					if (!pic.startsWith("http://") && FileKit.isFile(filePath)) {
						//上传封面
						fileService.upload(pic_key, filePath);
						//删除原有文件
						fileService.delete(focus.getPic());
					}
				}
				
				try {
					count = model.update().param("title", titleStr).param("introduce", introduceStr)
							.param("pic", pic_key).where("id", id).executeAndCommit();
					
				} catch (Exception e) {
					logger.warn("更新焦点图失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> getList(Integer type, String title, Integer status, String order) {
		List<Focus> list = model.select().where("type", type)
				.like("title", "%" + title).where("status", status).orderBy(order).fetchList();
		return this.getFocusMapList(list);
	}

	private List<Map<String, Object>> getFocusMapList(List<Focus> list) {
		List<Map<String, Object>> mapList = CollectionKit.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Focus focus = list.get(i);
			if (null != focus) {
				mapList.add(this.getMap(focus, null));
			}
		}
		return mapList;
	}

	@Override
	public Page<Focus> getPageList(Integer type, String title, Integer status, Integer page, Integer pageSize,
			String order) {
		return model.select().where("type", type)
		.like("title", "%" + title)
		.where("status", status)
		.orderBy(order).fetchPage(page, pageSize);
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer type, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		Page<Focus> pageList = this.getPageList(type, title, status, page, pageSize, order);

		List<Focus> focusList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getFocusMapList(focusList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public boolean enable(Integer id, Integer status) {
		if (null != id) {
			try {
				
				return model.update().param("status", status).where("id", id).executeAndCommit(Integer.class) > 0;
				
			} catch (Exception e) {
				logger.warn("删除焦点图失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

}
