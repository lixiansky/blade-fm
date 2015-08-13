package blade.fm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Component;
import blade.annotation.Inject;
import blade.fm.model.User;
import blade.fm.service.ActiveService;
import blade.fm.service.UserService;
import blade.fm.util.Base64;
import blade.fm.util.BeanUtil;
import blade.fm.util.EncrypUtil;
import blade.kit.CollectionKit;
import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;

@Component
public class UserServiceImpl implements UserService {

	private Model<User> model = new Model<User>(User.class);
	
	@Inject
	private ActiveService activeService;

	private User find(Integer uid, String email, Integer is_admin, Integer status) {
		return model.select().where("uid", uid).where("is_admin", is_admin)
				.where("email", email).where("status", status).fetchOne();
	}

	@Override
	public User getByUid(Integer uid) {
		return model.select().fetchByPk(uid);
	}

	@Override
	public User register(String nickname, String email, String password, String ip) {
		User user = null;
		//密码规则:md5(email+password)
		String md5pwd = EncrypUtil.md5(email + password);
		Integer currtime = DateKit.getUnixTimeByDate(new Date());
		int count = 0;
		try {
			count = model.insert()
					.param("nickname", nickname)
					.param("email", email)
					.param("password", md5pwd)
					.param("reg_ip", ip)
					.param("reg_time", currtime)
					.param("log_time", currtime)
					.param("status", 1).executeAndCommit();
		} catch (Exception e) {
			count = 0;
		}
		if (count > 0) {
			user = this.find(null, email, null, 1);
			// 生成激活码: sha1(email) 激活码
			String code = Base64.encoder(email);
			activeService.save(user.getUid(), code);
//			String url = "http://fm.im90.me/active?code="+code;
//			// 发送邮件
//			SendMail.asynSend("七牛云音乐电台激活帐号通知", 
//					"点击链接激活您的邮箱  <a herf='"+url+"' target='_blank'>"+url+"</a>", email);
		}
		return user;
	}

	@Override
	public boolean exists(String email) {
		return null == this.find(null, email, null, 1);
	}

	@Override
	public List<User> getList(String nickname, String email, Integer status, String order) {
		return model.select().like("nickname", "%" + nickname).like("email", "%" + email)
				.where("status", status).orderBy(order).fetchList();
	}

	@Override
	public Page<User> getPageList(String nickname, String email, Integer status, Integer page, Integer pageSize,
			String order) {
		return model.select().like("nickname", "%" + nickname).like("email", "%" + email)
				.where("status", status).orderBy(order).fetchPage(page, pageSize);
	}

	@Override
	public int delete(String email, Integer uid) {
		int count = 0;
		if (null != uid) {
			try {
				count = model.delete().where("uid", uid).executeAndCommit();
			} catch (Exception e) {
				count = 0;
			}
		}
		if (StringUtils.isNotBlank(email)) {
			try {
				count = model.delete().where("email", email).executeAndCommit();
			} catch (Exception e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int deleteBatch(String uids) {
		int count = 0;
		if (null != uids) {
			try {
				count = model.delete().in("uid", uids).executeAndCommit();
			} catch (Exception e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int enable(String email, Integer uid, Integer status) {
		if (null != uid) {
			return model.update().param("status", status).where("uid", uid).executeAndCommit();
		}
		if (StringUtils.isNotBlank(email)) {
			return model.update().param("status", status).where("email", email).executeAndCommit();
		}
		return 0;
	}

	@Override
	public User login(String email, String password) {
		if(StringKit.isNotBlank(email) && StringKit.isNotBlank(password)){
			String pwd = EncrypUtil.md5(email + password);
			
			return model.select()
					.where("email", email)
					.where("password", pwd)
					.where("is_admin", 1)
					.where("status", 1).fetchOne();
		}
		return null;
	}

	@Override
	public int updateUseSize(Integer uid, Long useSpace) {
		int count = 0;
		if (null != uid && null != useSpace) {
			try {
				count = model.update().param("use_size", "(u.use_size + "+useSpace+")").where("uid", uid).executeAndCommit();
			} catch (Exception e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public User get(String email, Integer status) {
		return this.find(null, email, null, status);
	}

	@Override
	public int update(Integer uid, String email, String nickName, Long space_size, Integer status) {
		int count = 0;
		try {
			count = model.update()
					.param("status", status)
					.param("nickName", nickName)
					.param("space_size", space_size)
					.where("uid", uid)
					.where("email", email)
					.executeAndCommit();
		} catch (Exception e) {
			count = 0;
		}
		return count;
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(String username, String email, Integer status, Integer page,
			Integer pageSize, String order) {
		Page<User> pageList = this.getPageList(username, email, status, page, pageSize, order);

		List<User> userList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount() , pageList.getPage(), pageList.getPageSize());

		List<Map<String, Object>> listMap = CollectionKit.newArrayList();
		for (int i = 0, len = userList.size(); i < len; i++) {
			User user = userList.get(i);
			if (null != user) {
				listMap.add(this.getMap(user, null));
			}
		}
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public Map<String, Object> getMap(User user, Integer uid) {
		Map<String, Object> resultMap = CollectionKit.newHashMap();
		if (null == user) {
			user = this.find(uid, null, null, null);
		}
		if (null != user) {
			resultMap = BeanUtil.toMap(user);
			if(null != user.getReg_time()){
				resultMap.put("reg_time_zh", DateKit.formatDateByUnixTime(user.getReg_time(), "yyyy/MM/dd HH:mm"));
			}
			if(null != user.getLog_time()){
				resultMap.put("last_login_time", DateKit.formatDateByUnixTime(user.getLog_time(), "yyyy/MM/dd HH:mm"));
			}
		}
		return resultMap;
	}

}
