package blade.fm.interceptor;

import java.math.BigDecimal;
import java.util.Map;

import blade.annotation.After;
import blade.annotation.Before;
import blade.annotation.Inject;
import blade.annotation.Interceptor;
import blade.fm.Constant;
import blade.fm.model.User;
import blade.fm.service.SettingService;
import blade.kit.log.Logger;
import blade.kit.timw.TimwManager;
import blade.kit.timw.TimwMonitor;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * 全局拦截器
 */
@Interceptor
public class BaseInterceptor {

	private static Logger LOGGER = Logger.getLogger(BaseInterceptor.class);
	
	@Inject
	private SettingService settingService;
	
	TimwMonitor monitor = TimwManager.getTimerMonitor();
	
	@Before("/*")
	public void before(Request request, Response response){
		
		LOGGER.info("IP=" + request.ip() + ",REFERER=" + request.header("Referer"));
		
		monitor.start();
		
		String assets = request.contextPath() + "/assets";
		
		String theme = assets + "/" + "theme";
		
		Map<String, String> setting =  settingService.getAllSetting();
		
		request.attribute("assets", assets);
		request.attribute("theme", theme);
		request.attribute("base", request.contextPath());
		request.attribute("static_v", "1.0");
		request.attribute("setting", setting);
		request.attribute("cdn", request.contextPath());
		
		User login_user = request.session().attribute(Constant.LOGIN_SESSION);
		
		// 未登录
		if(null == login_user && request.uri().indexOf("/admin") != -1){
			response.redirect(Constant.ADMIN_LOGIN);
			return;
		}
		request.attribute("login_user", login_user);
	}
	
	@After("/*")
	public void after(){
		monitor.end();
		BigDecimal b = new BigDecimal(monitor.avg().doubleValue());
		double avg = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		LOGGER.debug("执行了(" + monitor.size() + ")次，本次：" + monitor.current() + " ms，平均：" + avg + " ms");
	}
	
}
