package blade.fm.model;

import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Table;

/**
 * 开放平台关联表
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Table(value="t_open")
public class Open extends Model {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer type;
	private String email;
	private String openid;
	private Integer status;
	
	public Open(){
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}

