# SAMS 学生宿舍管理系统

SAMS 是一个基于 JFinal、Vue 3 和 MySQL 的宿舍管理系统，用于演示学生账号导入、宿舍成员维护、违规记录录入、申诉审核和学生权限授权等流程。

## 快速使用

1. 创建并填充数据库：

```sql
SOURCE database/schema01.sql;
SOURCE database/schema02.sql;
```

也可以执行兼容入口：

```sql
SOURCE database/schema.sql;
```

2. 修改数据库连接配置：

```text
src/main/resources/database.properties
```

3. 构建项目：

```bash
cd src
mvn package
```

4. 将生成的 `target/SAMS.war` 部署到 Tomcat，访问：

```text
http://localhost:8080/SAMS/
```

5. 使用演示账号登录。所有演示账号初始密码均为 `123456`。

| 身份 | 账号 | 手机号 | 说明 |
| --- | --- | --- | --- |
| 普通学生 | `26050140101` | `13805010101` | 人工智能学院，鱼科1班，01-101 |
| 学生干部示例 | `26050140102` | `13805010102` | 已有院级查看和录入权限 |
| 班级教师 | `90000000001` | `13900000001` | 只能管理人工智能学院鱼科1班 |
| 学院教师 | `90000000002` | `13900000002` | 可查看、审核、授权人工智能学院学生 |
| 校级教师 | `90000000003` | `13900000003` | 可查看、审核、授权全校学生 |
| 文传学院教师 | `90000000004` | `13900000004` | 可演示跨学院权限边界 |
| 电子信息学院教师 | `90000000005` | `13900000005` | 可管理电子信息学院学生 |
| 系统管理员 | `99999999999` | `13999999999` | 只能维护教师权限 |

## 演示数据

数据库初始化拆为两个文件：

- `database/schema01.sql`：只负责创建数据库和表结构。
- `database/schema02.sql`：只负责填充演示数据。

演示数据包含：

- 3 个教学院：文化与传播学院、人工智能学院、电子信息学院。
- 15 间宿舍，其中 12 间宿舍各 4 人，另外 3 间为空宿舍。
- 48 个已入住学生和 12 个未注册学生。
- 多条违规记录，以及待审核和已审核的申诉记录。

学号按现有规则解析：第 3-4 位为学院编号，第 5-6 位为专业编号，第 8-9 位为班级编号。页面展示时不会直接显示编号，而会显示学院名称和班级名称，例如：

- `05` + `01` + `01`：人工智能学院，鱼科1班。
- `04` + `01` + `01`：文化与传播学院，网媒1班。

## 功能概览

- 登录：学生可用学号或绑定手机号登录，教师可用工号或手机号登录，前端自动识别身份。
- 学生首次绑定：教师导入的学生首次登录后补充手机号和宿舍。
- 违规记录：按权限范围查看、筛选、录入和导出本周汇总。
- Excel 模板：违规记录导入和学生导入都提供模板下载。
- 学生导入：教师可以用网页表格逐行填写，也可以上传 Excel。
- 学生检索：学生与宿舍、学生授权页面均可按学号、手机号、姓名或 `14-524` 格式的宿舍检索，不检索时展示权限范围内全部学生。
- 大列表检索：教师权限、教学院管理、班级管理等数据量可能增长的页面均提供后端检索。
- 密码重置：教师可将管理范围内学生密码重置为 `123456`。
- 宿舍维护：教师可以调整学生宿舍，系统会按学生所属学院维护宿舍归属。
- 申诉审核：学生可对本寝室违规记录申诉，具备审核权限的账号处理申诉。
- 学生授权：教师必须先通过手机号、学号或姓名检索学生，再为检索结果授予模块权限。
- 组织管理：按教师权限维护教学院，以及本院或全校班级信息。

API 细节见 `API.md`，README 不再重复接口清单。

## 技术栈

- Java 21
- JFinal 5.2.5
- MySQL 8
- Druid 数据库连接池
- JFinal ActiveRecord
- Jackson
- Apache POI
- Vue 3
- Tomcat / Servlet 3.1 容器
- Maven WAR 构建

## JFinal 在项目中的作用

本项目仍然部署在 Tomcat 这样的 Servlet 容器中，但业务代码不再以“一条功能一个 Servlet”的方式组织。`web/WEB-INF/web.xml` 将请求交给 JFinal 的核心过滤器，JFinal 再根据 `BaseConfig` 中的路由表分发到 Controller。

这种结构下，JFinal 取代了手写 Servlet 的大部分职责：

- 路由分发：`BaseConfig.configRoute` 将 `/auth`、`/student`、`/violation` 等路径映射到对应 Controller。
- 请求读取：Controller 通过 `getPara`、`getFile`、`getRawData` 等能力读取 query、上传文件和 JSON。
- 响应渲染：统一使用 `renderJson`、`renderFile` 输出 JSON 和 Excel 文件。
- 拦截器：`AuthInterceptor` 负责登录态校验，`ApiExceptionInterceptor` 负责把业务异常转换为统一 JSON。
- 数据访问：ActiveRecordPlugin 绑定数据表和 Model，也可以直接用 `Db.find`、`Db.update` 执行 SQL。

旧的 `src/main/java/servlet` 目录中仍保留了一些 Servlet 类，主要用于兼容或历史参考；当前核心业务入口是 `controller` 包下的 JFinal Controller。

## 项目结构

```text
SAMS/
├── API.md                         独立 API 文档
├── DRequir.md                     需求说明
├── README.md                      项目说明
├── database/
│   ├── schema01.sql               数据库建库建表
│   ├── schema02.sql               演示数据
│   └── schema.sql                 兼容入口，顺序执行 01 和 02
├── web/
│   ├── index.html                 Vue 单页入口
│   ├── assets/
│   │   ├── app.js                 前端状态、请求和交互逻辑
│   │   ├── app.css                页面样式
│   │   └── vue.global.prod.js     Vue 运行时
│   └── WEB-INF/
│       └── web.xml                JFinal 入口配置
└── src/
    ├── pom.xml                    Maven WAR 配置
    ├── main/
    │   ├── java/
    │   │   ├── base/              JFinal 配置和基础 Controller
    │   │   ├── controller/        业务 Controller
    │   │   ├── exception/         业务异常
    │   │   ├── intercept/         登录和异常拦截器
    │   │   ├── model/             ActiveRecord Model
    │   │   ├── service/           权限、认证、密码等服务
    │   │   └── servlet/           历史 Servlet 代码
    │   └── resources/
    │       ├── config.properties
    │       ├── database.properties
    │       └── log4j2.xml
    └── test/
        └── java/                  单元测试
```

## 开发备注

- 前端是静态 Vue 3 页面，不需要单独的 Node 构建步骤。
- 后端上传和导出 Excel 均使用 Apache POI。
- 权限分为学生权限和教师权限，实际边界由 `AccessService` 统一判断。
- 组织管理权限包含 `TEACHER_MANAGE_COLLEGE`、`TEACHER_MANAGE_CLASS_COLLEGE` 和 `TEACHER_MANAGE_CLASS_SCHOOL`；系统管理员权限为 `SYSTEM_MANAGE_TEACHER_PERMISSION`。
- 数据库脚本中的演示密码使用明文 `123456`，首次成功登录后会自动升级为 PBKDF2 摘要。
