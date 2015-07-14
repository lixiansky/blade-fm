package blade.fm.route.admin;


import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.Constant;
import blade.fm.route.BaseRoute;
import blade.fm.service.UserService;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 用户后台首页
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Path("/admin")
public class IndexRoute extends BaseRoute {

	@Inject
	private UserService userService;
	
	/**
	 * 后台首页
	 */
	@Route("/")
	public String home(Request request) {
		return index(request);
	}
	
	/**
	 * 后台首页
	 */
	@Route("index")
	public String index(Request request) {
		return "index";
	}

	/**
	 * 管理员退出
	 */
	@Route("logout")
	public void logout(Request request, Response response) {
		request.session().removeAttribute(Constant.LOGIN_SESSION);
		response.redirect("/login");
	}
	
}
