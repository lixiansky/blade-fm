package blade.fm.service.impl;

import blade.annotation.Component;
import blade.fm.model.Open;
import blade.fm.service.OpenService;
import blade.kit.log.Logger;

@Component
public class OpenServiceImpl implements OpenService {
	
	private Logger logger = Logger.getLogger(OpenServiceImpl.class);
	
	private Open model = new Open();
	
	private Open find(Integer id, String email, String openid,
			Integer type, Integer status){
		
        return model.select().where("id", id).where("email", email).where("openid", openid)
        .where("type", type).where("status", status).fetchOne();
	}
	
	@Override
	public Open get(String email, String openid, Integer type) {
		return this.find(null, email, openid, type, 1);
	}

	@Override
	public int save(String email, Integer type, String openid) {
		int count = 0;
		try {
			count = model.insert().param("type", type)
					.param("email", email)
					.param("openid", openid)
					.param("status", 1).executeAndCommit(Integer.class);
		} catch (Exception e) {
			logger.warn("保存open用户失败：" + e.getMessage());
			count = 0;
		}
		return count;
	}

}
