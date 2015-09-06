package blade.fm;

import blade.Blade;
import blade.Bootstrap;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.DBConfig;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.render.BeetlRender;
	
public class App extends Bootstrap {

	static Logger logger = Logger.getLogger(App.class);
	
	@Override
	public void init() {
		
		Blade.config("blade.properties");
		
		// 设置模板引擎
		BeetlRender beetlRender = new BeetlRender();
		
		Blade.viewEngin(beetlRender);
		
		// 配置数据库插件
		String driverName = Blade.config().get("blade.dbdriver");
		String url = Blade.config().get("blade.dburl");
		String user = Blade.config().get("blade.dbuser");
		String pass = Blade.config().get("blade.dbpass");
		String opencache = Blade.config().get("blade.opencache");
		Boolean cache = Boolean.valueOf(StringKit.defaultIfNull(opencache, "false"));
		
		DBConfig dbConfig = new DBConfig();
		dbConfig.setUrl(url);
		dbConfig.setUserName(user);
		dbConfig.setPassWord(pass);
		dbConfig.setDriverName(driverName);
		
		Blade.plugin(Sql2oPlugin.class).config(dbConfig).cache(cache).run();
		
	}
	
}