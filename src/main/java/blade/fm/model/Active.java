package blade.fm.model;

import java.io.Serializable;

import blade.plugin.sql2o.Table;


/**
 * 激活表
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Table(value="t_active")
public class Active implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer uid;
	private Integer status;

	public Active(){
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}

