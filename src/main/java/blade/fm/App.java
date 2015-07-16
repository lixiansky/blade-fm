package blade.fm;

import java.util.HashMap;
import java.util.Map;

import blade.Blade;
import blade.BladeApplication;
import blade.kit.PropertyKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.render.BeetlRender;

public class App extends BladeApplication {

	static Logger logger = Logger.getLogger(App.class);
	
	@Override
	public void init() {
		
		// 设置路由、拦截器包所在包
		Blade.routes("blade.fm.route.*");
		
		Blade.interceptor("blade.fm.interceptor");
		
		// 设置要扫描的ioc包，可选
		Blade.ioc("blade.fm.service.*");
		
		// 设置视图位置和默认后缀名称
		Blade.view("/WEB-INF/views/", ".html");
		
		// 设置静态目录
		Blade.staticFolder("/assets/", "/userfiles/");
		
		// 设置模板引擎
		BeetlRender beetlRender = new BeetlRender();
		
		Blade.viewEngin(beetlRender);
		
		// 配置数据库插件
		Sql2oPlugin sql2oPlugin = Sql2oPlugin.INSTANCE;
		sql2oPlugin.config(bladeConf.get("BLADE.DBURL"), bladeConf.get("BLADE.DBDRIVER"), bladeConf.get("BLADE.DBUSER"), bladeConf.get("BLADE.DBPASS"));
		sql2oPlugin.openCache();
		sql2oPlugin.run();
		
	}
	
	static Map<String, String> bladeConf = new HashMap<String, String>();
	
	static{
		bladeConf = PropertyKit.getPropertyMap("blade.properties");
		System.out.println(bladeConf);
	}
	
	public static void main(String[] args) {
		Blade.run(App.class, 9000);
	}
	
}