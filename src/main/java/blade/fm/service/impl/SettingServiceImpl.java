package blade.fm.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Component;
import blade.fm.model.Setting;
import blade.fm.service.SettingService;
import blade.kit.CollectionKit;
import blade.plugin.sql2o.Model;

@Component
public class SettingServiceImpl implements SettingService {
	
	private Model<Setting> model = new Model<Setting>(Setting.class);
	
	@Override
	public Setting get(String key) {
        return model.select().where("skey", key).fetchOne();
	}

	@Override
	public int save(String key, String value) {
		int count = 0;
		try {
			if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
				Setting setting = this.get(key);
				if(null != setting){
					if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
						try {
							count = model.update().param("svalue", value).where("id", setting.getId()).executeAndCommit();
						} catch (Exception e) {
							e.printStackTrace();
							count = 0;
						}
					}
				} else{
					count = model.insert().param("skey", key).param("svalue", value).executeAndCommit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = 0;
		}
		return count;
	}

	@Override
	public int update(String key, String value) {
		int count = 0;
		if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
			try {
				count = model.update().param("svalue", value).where("skey", key).executeAndCommit();
			} catch (Exception e) {
				e.printStackTrace();
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int delete(String key) {
		int count = 0;
		if(StringUtils.isNotBlank(key)){
			try {
				count = model.delete().where("skey", key).executeAndCommit();
			} catch (Exception e) {
				e.printStackTrace();
				count = 0;
			}
		}
		return count;
	}
	
	@Override
	public Map<String, String> getAllSetting() {
		Map<String, String> map = CollectionKit.newHashMap();
		List<Setting> settingList = model.select().fetchList();
		for(Setting setting : settingList){
			map.put(setting.getSkey(), setting.getSvalue());
		}
		return map;
	}

}
