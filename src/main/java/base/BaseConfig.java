package base;
import controller.AdminController;
import controller.AuthController;
import controller.GlobalController;
import controller.HomeController;
import controller.OrgController;
import controller.StudentViolationController;
import controller.StudentController;
import model.Appeal;
import model.College;
import model.RememberToken;
import model.StudentViolation;
import model.Teacher;
import model.User;

import com.jfinal.config.JFinalConfig;

import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidPlugin;
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
        routes.add("/", HomeController.class);
        routes.add("/user",UserController.class);
        routes.add("/auth", AuthController.class);
        routes.add("/admin", AdminController.class);
        routes.add("/violation", StudentViolationController.class);
        routes.add("/student", StudentController.class);
        routes.add("/org", OrgController.class);
        routes.add("/global", GlobalController.class);
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
        DruidPlugin dp = new DruidPlugin(url, dbusr, dbpswd);
        plugins.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        // 开启 SQL 打印，方便调试
        arp.setShowSql(true);
        // 将 ActiveRecord 插件添加到容器中
        plugins.add(arp);
        // (重要) 绑定表名和 Model 类
        arp.addMapping("college", "college_id", College.class);
        arp.addMapping("student_info", "student_id", User.class);
        arp.addMapping("teacher_info", "teacher_id", Teacher.class);
        arp.addMapping("violation_info", "violation_id", StudentViolation.class);
        arp.addMapping("appeal_info", "appeal_id", Appeal.class);
        arp.addMapping("remember_token", "token_id", RememberToken.class);
    }

    @Override
    public void configInterceptor(com.jfinal.config.Interceptors interceptors) {

    }


    @Override
    public void configHandler(Handlers handlers) {

    }
}
