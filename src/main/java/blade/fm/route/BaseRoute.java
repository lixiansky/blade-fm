package blade.fm.route;

import blade.render.ModelAndView;

public class BaseRoute {
	
	protected Integer page = 1;
	
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
	
	protected Integer uid = 1;
}
