package blade.fm.model;

import java.io.Serializable;

import blade.plugin.sql2o.Table;

/**
 * 音频专辑
 * @author:rex
 * @date:2014年9月23日
 * @version:1.0
 */
@Table(value = "t_special")
public class Special implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer uid;
	private String title;
	private Integer type;
	private String introduce;
	private String cover;
	private Integer is_fine;
	private Integer hit;
	private Integer create_time;
	private Integer last_time;
	private Integer status;

	public Special() {
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public Integer getHit() {
		return hit;
	}

	public void setHit(Integer hit) {
		this.hit = hit;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getLast_time() {
		return last_time;
	}

	public void setLast_time(Integer last_time) {
		this.last_time = last_time;
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

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIs_fine() {
		return is_fine;
	}

	public void setIs_fine(Integer is_fine) {
		this.is_fine = is_fine;
	}

}
