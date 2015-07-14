package blade.fm.model;

import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Table;

/**
 * 相册
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Table(value="t_setting")
public class Setting extends Model {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String skey;
	private String svalue;
	private String sdesc;

	public Setting(){
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	public String getSdesc() {
		return sdesc;
	}

	public void setSdesc(String sdesc) {
		this.sdesc = sdesc;
	}

}

