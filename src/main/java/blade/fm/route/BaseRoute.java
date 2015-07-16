package blade.fm.route;

import blade.fm.Constant;
import blade.fm.model.User;
import blade.render.ModelAndView;
import blade.servlet.Request;

public class BaseRoute {
	
	public static final String MSG_SUCCESS = "success";
	
	public static final String MSG_FAILURE = "failure";
	
	public static final String MSG_ERROR = "error";
	
	public static final String MSG_VERIFY_ERROR = "verify_error";
	
	public static final String MSG_EXIST = "exist";
	
	protected Integer pageSize = 10;
	
	public ModelAndView getAdminModelAndView(String view){
		view = "/admin/" + view;
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
