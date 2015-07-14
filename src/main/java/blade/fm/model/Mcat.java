package blade.fm.model;

import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Table;

/**
 * 音乐分类
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Table(value="t_mcat")
public class Mcat extends Model {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Integer status;

	public Mcat(){
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getStatus() {
		return this.status;
	}

}

