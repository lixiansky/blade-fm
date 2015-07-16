package blade.fm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.Blade;
import blade.annotation.Component;
import blade.fm.QiniuApi;
import blade.fm.model.Album;
import blade.fm.service.AlbumService;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.FileKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Page;

import com.alibaba.fastjson.JSON;

@Component
public class AlbumServiceImpl implements AlbumService {

	private Logger logger = Logger.getLogger(AlbumServiceImpl.class);
	
	private Album model = new Album();
	
	@Override
	public Album get(Integer id) {
        return model.fetchByPk(id);
	}

	@Override
	public Map<String, Object> getMap(Album picture, Integer id) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == picture) {
			picture = this.get(id);
		}
		if (null != picture) {
			resultMap.put("id", picture.getId());
			resultMap.put("uid", picture.getUid());
			resultMap.put("title", picture.getTitle());
			resultMap.put("introduce", picture.getIntroduce());
			resultMap.put("status", picture.getStatus());
			
			if(StringUtils.isNotBlank(picture.getPics())){
				List<Map<String, Object>> pics = JSON.parseObject(picture.getPics(), List.class);
				resultMap.put("pics", pics);
			}
			if(StringUtils.isNotBlank(picture.getCover())){
				resultMap.put("cover_url", QiniuApi.getUrlByKey(picture.getCover()));
			}
			if(null != picture.getCreate_time()){
				resultMap.put("time_zh", DateKit.formatDateByUnixTime(picture.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			}
		}
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String title, Integer status, String order) {
		
		List<Album> list =model.select().where("uid", uid)
		.like("title", "%" + title)
		.where("status", status).orderBy(order).fetchList();
		return this.getPicMapList(list);
	}

	private List<Map<String, Object>> getPicMapList(List<Album> list) {
		List<Map<String, Object>> mapList = CollectionKit.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Album picture = list.get(i);
			if (null != picture) {
				mapList.add(this.getMap(picture, null));
			}
		}
		return mapList;
	}
	
	public Page<Album> getPageList(Integer uid, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		
		return model.select().where("uid", uid).like("title", "%" + title)
		.where("status", status).orderBy(order).fetchPage(page, pageSize);
		
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		
		Page<Album> pageList = this.getPageList(uid, title, status, page, pageSize, order);

		List<Album> pictureList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());
		
		List<Map<String, Object>> listMap = this.getPicMapList(pictureList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public boolean save(Integer uid, String title, String introduce, String cover, String pics, Integer status) {
		int count = 0, upCount = 0;
		uid = (null == uid) ? 1 : uid;
		if(StringUtils.isNotBlank(pics)){
			List<Map<String, Object>> picList = JSON.parseObject(pics, List.class);
			for(int i=0,len=picList.size(); i<len; i++){
				Map<String, Object> map = picList.get(i);
				if(map.size() > 0){
					String key = map.get("savepath").toString();
					String filePath = Blade.webRoot() + "/" + key;
					if (FileKit.isFile(filePath)) {
						//上传图片
//						fileService.upload(key, filePath);
						map.put("key", key);
						map.put("url", QiniuApi.getUrlByKey(key));
						upCount++;
					}
					map.remove("savepath");
					if(null == cover && i == 0){
						cover = key;
					}
				} else{
					picList.remove(map);
				}
			}
			String picJson = JSON.toJSONString(picList);
			try {
				if(upCount > 0){
					Integer create_time = DateKit.getUnixTimeByDate(new Date());
					count = model.insert()
							.param("uid", uid)
							.param("title", title)
							.param("introduce", introduce)
							.param("cover", cover)
							.param("pics", picJson)
							.param("status", status)
							.param("create_time", create_time).executeAndCommit(Integer.class);
				}
			} catch (Exception e) {
				logger.warn("保存图库失败：" + e.getMessage());
				count = 0;
			}
		}
		return count > 0;
	}

	@Override
	public boolean delete(Integer id) {
		if (null != id) {
			try {
				return model.delete().where("id", id).executeAndCommit(Integer.class) > 0;
			} catch (Exception e) {
				logger.warn("删除图库失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean update(Integer id, String title, String introduce, String cover, String pics, Integer status) {
		if(null != id){
			Album pic = this.get(id);
			int upCount = 0;
			if(null != pic){
				String coverStr = null;
				String picJson = null;
				if(StringUtils.isNotBlank(pics)){
					
					List<Map<String, Object>> picList = JSON.parseObject(pics, List.class);
					List<Map<String, Object>> newList = CollectionKit.newArrayList();
					for(int i=0,len=picList.size(); i<len; i++){
						Map<String, Object> map = picList.get(i);
						if(map.size() > 0){
							String key = map.get("savepath").toString();
							if(key.startsWith("upload/images/") && null != map.get("isold")){
								map.put("key", key);
								map.put("url", QiniuApi.getUrlByKey(key));
								upCount++;
								newList.add(map);
							} else{
								String filePath = Blade.webRoot() + "/" + key;
								if (FileKit.isFile(filePath)) {
									//上传图片
//									fileService.upload(key, filePath);
									map.put("key", key);
									map.put("url", QiniuApi.getUrlByKey(key));
									upCount++;
									newList.add(map);
								}
							}
							map.remove("savepath");
							if(StringUtils.isBlank(cover) && StringUtils.isNotBlank(key) && i == 0){
								coverStr = key;
							}
						}
					}
					picJson = JSON.toJSONString(newList);
				}

				try {
					model.update()
					.param("title", title)
					.param("introduce", introduce)
					.param("cover", coverStr)
					.param("title", title)
					.param("pics", picJson)
					.param("status", status)
					.where("id", id).executeAndCommit();
					
					return true;
				} catch (Exception e) {
					logger.warn("更新图库失败：" + e.getMessage());
				}
			}
		}
		return false;
	}

}
