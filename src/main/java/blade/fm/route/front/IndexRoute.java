package blade.fm.route.front;

import blade.annotation.Inject;
import blade.annotation.Path;
import blade.annotation.Route;
import blade.fm.route.BaseRoute;
import blade.fm.service.OpenService;
import blade.fm.service.UserService;
import blade.kit.log.Logger;
import blade.servlet.Request;

/**
 * 前台首页
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
@Path("/")
public class IndexRoute extends BaseRoute {

	private Logger logger = Logger.getLogger(IndexRoute.class);
			
	@Inject
	private UserService userService;
	@Inject
	private OpenService openService;
	
	@Route("index")
	public String index(Request request) {
		return "index";
	}

	@Route("up")
	public String up(){
		return "up";
	}
	
	/**
	 * 用qq登录
	 */
	/*@Route("qq_login")
	public void qq_login(Request request, Response response) {
		try {
			response.redirect(new Oauth().getAuthorizeURL(request.servletRequest()));
		} catch (QQConnectException e) {
			logger.warn(e.getMessage());
		}
	}*/

	/**
	 * 用qq登录回调
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	/*@Route("qq_callback")
	public void qq_callback(Request request, Response response) {
		try {
			AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request.servletRequest());

			String accessToken = null, openID = null;
			long tokenExpireIn = 0L;

			if (accessTokenObj.getAccessToken().equals("")) {
				// 我们的网站被CSRF攻击了或者用户取消了授权做一些数据统计工作
				System.out.print("没有获取到响应参数");
				response.redirect("/admin/login");
			} else {
				accessToken = accessTokenObj.getAccessToken();
				tokenExpireIn = accessTokenObj.getExpireIn();
				
				Constant.QQ_TOKEN = accessToken;
				Constant.QQ_TOKEN_EXPIREIN = tokenExpireIn;
				
				// 利用获取到的accessToken 去获取当前用的openid -------- start
				OpenID openIDObj = new OpenID(accessToken);
				openID = openIDObj.getUserOpenID();
				
				//去数据库查询是否绑定openid
				Open openUser = openService.get(null, openID, 1);
				if(null != openUser){
					User user = userService.get(openUser.getEmail(), 1);
					if(null != user){
						request.session().attribute(Constant.LOGIN_SESSION, user);
						response.redirect("/admin/index");
					}
				} else{
					UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
					UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
					request.attribute("openid", openID);
					request.attribute("nickname", userInfoBean.getNickname());
					response.redirect("/bind_qq");
				}
			}
		} catch (QQConnectException e) {
			logger.warn(e.getMessage());
		}
	}*/
	
	/**
	 * 确认绑定qq
	 */
	/*@Route("save_bind_qq")
	public void save_bind_qq(Request request, Response response){
		String openId = request.query("openid");
		String email = request.query("email");
		String nickname = request.query("nickname");
		String password = request.query("password");
		if(StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(email)){
			if(null == openService.get(email, null, 1)){
				int count = openService.save(email, 1, openId);
				if(count > 0){
					User user = userService.get(email, 1);
					//登录管理员 放入session
					if (null != user) {
						request.session().attribute(Constant.LOGIN_SESSION, user);
						response.text(Constant.MSG_SUCCESS);
					} else {
						user = userService.register(nickname, email, password, IpKit.getIpAddrByRequest(request.servletRequest()));
						if(null != user){
							request.session().attribute(Constant.LOGIN_SESSION, user);
							response.text(Constant.MSG_SUCCESS);
						} else{
							response.text(Constant.MSG_FAILURE);
						}
					}
				} else{
					response.text(Constant.MSG_FAILURE);
				}
			} else{
				// 已经绑定过
				response.text(Constant.MSG_EXIST);
			}
			return;
		}
		response.text(Constant.MSG_ERROR);
	}*/

}
