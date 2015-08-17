package blade.fm;

import blade.Blade;
import blade.BladeApplication;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.render.BeetlRender;

public class App extends BladeApplication {

	static Logger logger = Logger.getLogger(App.class);
	
	@Override
	public void init() {
		
		Blade.config("blade.properties");
		
		// 设置模板引擎
		BeetlRender beetlRender = new BeetlRender();
		
		Blade.viewEngin(beetlRender);
		
		// 配置数据库插件
		Sql2oPlugin.INSTANCE.autoConfig().run();
		
	}
	
}