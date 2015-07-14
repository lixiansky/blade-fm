package blade.fm.route;

import blade.fm.Constant;
import blade.fm.model.User;
import blade.render.ModelAndView;
import blade.servlet.Request;

public class BaseRoute {
	
	protected Integer pageSize = 10;
	
	// 成功
	protected final String SUCCESS = "success";
	
	// 服务器异常
	protected final String ERROR = "error";
	
	// 已经存在
	public final String EXIST = "exist";
	
	// 失败
	protected final String FAILURE = "failure";
	
	public ModelAndView getFrontModelAndView(String view){
		view = "/theme/" + view + ".html";
		return new ModelAndView(view);
	}
	
	public ModelAndView getAdminModelAndView(String view){
		view = "/admin/" + view + ".html";
		return new ModelAndView(view);
	}
	
	public Integer getUid(Request request){
		User user = request.session().attribute(Constant.LOGIN_SESSION);
		if(null != user){
			return user.getUid();
		}
		return 1;
	}
}
