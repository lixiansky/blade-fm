package blade.fm.cloud.model;

import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Table;

/**
 * 焦点图
 * @author:rex
 * @date:2014年10月10日
 * @version:1.0
 */
@Table(value = "t_focus")
public class Focus extends Model {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;
	private String introduce;
	private String pic;
	private Integer type;
	private Integer create_time;
	private Integer status;

	public Focus() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
}
