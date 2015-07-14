package blade.fm.service.impl;

import java.io.UnsupportedEncodingException;

import blade.fm.model.Active;
import blade.fm.service.ActiveService;
import blade.fm.service.UserService;
import blade.fm.util.Base64;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.kit.log.Logger;

@Component
public class ActiveServiceImpl implements ActiveService {

	private Logger logger = Logger.getLogger(ActiveServiceImpl.class);
	
	private Active model = new Active();
	
	@Inject
	private UserService userService;
	
	@Override
	public int save(Integer uid, String code) {
		int count = 0;
		try {
			count = model.insert()
					.param("uid", uid)
					.param("code", code)
					.param("status", 1)
					.executeAndCommit();
		} catch (Exception e) {
			logger.warn("添加激活码失败：" + e.getMessage());
			count = 0;
		}
		return count;
	}

	@Override
	public Active get(Integer uid, String code) {
		
		return model.select().where("uid", uid).where("code", code).fetchOne();
		
	}

	@Override
	public void active(String code) {
		try {
			String email = Base64.decoder(code);
			userService.update(null, email, null, null, 1);
		} catch (UnsupportedEncodingException e) {
			logger.warn("激活激活码失败：" + e.getMessage());
		}
	}

}
