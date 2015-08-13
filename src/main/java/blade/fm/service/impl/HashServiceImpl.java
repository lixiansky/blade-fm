package blade.fm.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Component;
import blade.fm.model.Hash;
import blade.fm.service.HashService;
import blade.kit.DateKit;
import blade.plugin.sql2o.Model;

@Component
public class HashServiceImpl implements HashService {
	
	private Model<Hash> model = new Model<Hash>(Hash.class);
	
	@Override
	public Hash get(Integer id, String hash) {
		if (null != id) {
			return model.select().fetchByPk(id);
		}
		if (StringUtils.isNotBlank(hash)) {
			return model.select().where("status", 1).where("hash_code", hash).fetchOne();
		}
		return null;
	}

	@Override
	public boolean save(String fileName, String savePath, Long length, String hash, String key) {
		if (StringUtils.isNotBlank(hash) && StringUtils.isNotBlank(key)) {
			Integer dateline = DateKit.getUnixTimeByDate(new Date());
			return model.insert()
			.param("hash_code", hash)
			.param("file_key", key)
			.param("file_name", fileName)
			.param("save_path", savePath)
			.param("length", length)
			.param("dateline", dateline).executeAndCommit(Integer.class) > 0;
		}
		return false;
	}

}
