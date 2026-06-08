package base;
import model.User;

import com.jfinal.config.JFinalConfig;

import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import controller.UserController;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

public class BaseConfig extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        System.out.println(">>> JFinal Booting... <<<"); // 手动打印确认执行到这里
        me.setDevMode(true); // 开启开发模式
    }

    @Override
    public void configRoute(Routes routes) {

        System.out.println("configRoute init!");
        routes.add("/user",UserController.class);


    }


    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        Prop prop = PropKit.use("database.properties");

        // 从已加载的 Prop 对象中获取属性
        String url = prop.get("student_info_url_mysql");
        String dbusr = prop.get("student_info_usr_mysql");
        String dbpswd = prop.get("student_info_pswd_mysql");
        String driver = prop.get("student_info_driver_mysql");

        DruidPlugin dp = new DruidPlugin(url, dbusr, dbpswd);
        plugins.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        // 开启 SQL 打印，方便调试
        arp.setShowSql(true);
        // 将 ActiveRecord 插件添加到容器中
        plugins.add(arp);
        // (重要) 绑定表名和 Model 类
//        假设你的表名是 student_info，主键是 id
        arp.addMapping("student_info", User.class);
    }

    @Override
    public void configInterceptor(com.jfinal.config.Interceptors interceptors) {

    }


    @Override
    public void configHandler(Handlers handlers) {

    }
}
