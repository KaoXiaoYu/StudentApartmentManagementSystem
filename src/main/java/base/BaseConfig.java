package base;

import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import controller.UserController;

public class BaseConfig extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        System.out.println(">>> JFinal 正在启动... <<<"); // 手动打印确认执行到这里
        me.setDevMode(true);
    }

    @Override
    public void configRoute(Routes routes) {
        System.out.println("有访问！");
        routes.add("/",UserController.class);

        //routes.add("/login", LoginServlet.class);

    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
//        String url= PropKit.use("database.properties").get("student_info_url_mysql");
//        String dbusr=PropKit.use("database.properties").get("student_info_usr_mysql");
//        String dbpswd=PropKit.use("database.properties").get("student_info_pswd_mysql");
//        String driver=PropKit.use("database.properties").get("student_info_driver_mysql");
//
//        DruidPlugin Dp = new DruidPlugin(url,dbusr, dbpswd);
//        plugins.add(Dp); // 将插件添加到 JFinal 容器中
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {

    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
