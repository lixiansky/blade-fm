package blade.fm.route;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.Constant;
import blade.fm.model.User;
import blade.fm.service.UserService;
import blade.render.ModelAndView;
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
	
	@Route("/")
	public String index(Request request) {
		return "index";
	}
	
	@Route("up")
	public String up(){
		return "up";
	}
	
	@Route(value = "login")
	public ModelAndView loginPage(Request request, Response response){
		ModelAndView modelAndView = new ModelAndView("login");
		return modelAndView;
	}
	
	/**
	 * 用户登录
	 */
	@Route(value = "login")
	public String login(Request request, Response response){
		
		String step = request.query("step");
		
		if (null != step && step.equals("login")) {
			
			String login_name = request.query("login_name");
			String verify_code = request.query("verify_code");
			String pass_word = request.query("pass_word");
			String currentcode = request.session().attribute(Constant.CAPTCHA_TOKEN);
			
			if(StringUtils.isNotBlank(verify_code) && verify_code.equalsIgnoreCase(currentcode)){
				User user = userService.login(login_name, pass_word);
				//登录成功
				if (null != user) {
					request.session().attribute(Constant.LOGIN_SESSION, user);
					response.text(MSG_SUCCESS);
				} else {
					response.text(MSG_ERROR);
				}
				return null;
			} else{
				response.text(MSG_VERIFY_ERROR);
				return null;
			}
		}
		return "login";
	}
	
	@Route(value = "random")
	public ModelAndView random(){
		ModelAndView modelAndView = new ModelAndView("random");
		return modelAndView;
	}
}

