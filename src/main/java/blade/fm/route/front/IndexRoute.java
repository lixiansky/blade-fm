package blade.fm.route.front;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.Constant;
import blade.fm.model.User;
import blade.fm.route.BaseRoute;
import blade.fm.service.OpenService;
import blade.fm.service.UserService;
import blade.render.ModelAndView;
import blade.route.HttpMethod;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 前台首页
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
@Path
public class IndexRoute extends BaseRoute {

	@Inject
	private UserService userService;
	@Inject
	private OpenService openService;
	
	@Route("/")
	public String index(Request request) {
		return "index";
	}
	
	@Route("/index")
	public String home(Request request) {
		return index(request);
	}

	@Route("up")
	public String up(){
		return "up";
	}
	
	@Route(value = "/login")
	public ModelAndView loginPage(Request request, Response response){
		ModelAndView modelAndView = new ModelAndView("login");
		return modelAndView;
	}
	
	/**
	 * 用户登录
	 */
	@Route(value = "/login", method = HttpMethod.POST)
	public ModelAndView login(Request request, Response response){
		
		String step = request.query("step");
		ModelAndView modelAndView = new ModelAndView("login");
		
		if (null != step && step.equals("login")) {
			String login_name = request.query("login_name");
			String verify_code = request.query("verify_code");
			String pass_word = request.query("pass_word");
			String currentcode = "";
			
			if(StringUtils.isNotBlank(verify_code) && verify_code.equalsIgnoreCase(currentcode)){
				User user = userService.login(login_name, pass_word);
				//登录成功
				if (null != user) {
					request.session().attribute(Constant.LOGIN_SESSION, user);
					response.redirect("/admin/index");
				} else {
					modelAndView.add(this.ERROR, "用户名或者密码错误！");
				}
			} else{
				modelAndView.add(this.ERROR, "用户名或者密码错误！");
			}
		}
		
		return modelAndView;
	}
}

