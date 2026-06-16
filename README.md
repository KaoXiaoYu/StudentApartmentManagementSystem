# SAMS 学生宿舍管理系统

SAMS 是一个基于 JFinal 5、Vue 3 和 MySQL 的学生宿舍管理系统，用于管理宿舍违规记录、学生申诉、教师导入学生账号、宿舍成员维护，以及按模块自由组合的学生/教师权限。

当前版本已经从旧的“普通学生/学生干部、学院教师/校级教师”固定角色，改为“基础账号能力 + 权限模块”的设计。

## 运行环境

- JDK 21
- Maven 3.8+
- MySQL 8.0+
- Tomcat 9 或其他兼容 Servlet 3.1 的容器
- 浏览器需要支持现代 JavaScript；前端使用 `web/assets/vue.global.prod.js` 本地文件，不依赖运行时外网

## 快速启动

1. 创建数据库并初始化表结构。

```sql
SOURCE database/schema.sql;
```

也可以在 MySQL 客户端中打开并执行 [database/schema.sql](database/schema.sql)。

2. 修改数据库连接配置。

文件位置：[src/main/resources/database.properties](src/main/resources/database.properties)

需要确认以下配置指向本机 MySQL：

```properties
student_info_url_mysql=jdbc:mysql://localhost:3306/sams?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
student_info_usr_mysql=root
student_info_pswd_mysql=你的密码
```

3. 编译并打包。

```bash
mvn -f src/pom.xml clean package
```

4. 部署到 Tomcat。

把生成的 `src/target/SAMS.war` 放入 Tomcat 的 `webapps` 目录，启动后访问：

```text
http://localhost:8080/SAMS/
```

如果使用 IDE 直接运行 Tomcat，请把 Web 资源目录指向 `web`，后端 classpath 使用 `src/main/java` 和 `src/main/resources`。

## 初始化数据

[database/schema.sql](database/schema.sql) 会创建并初始化：

- 学院：`05` 人工智能学院、`06` 电子信息学院
- 宿舍：`05-01-101`、`05-01-102`、`05-02-201`、`06-03-101`
- 权限字典：7 个学生权限、6 个教师权限
- 演示学生、教师账号

所有演示账号初始密码都是：

```text
123456
```

首次登录后，如果密码仍是明文兼容值，后端会自动升级为 PBKDF2 摘要。

## 演示账号

| 类型 | 账号 | 手机号 | 说明 |
| --- | --- | --- | --- |
| 普通学生 | `26050540101` | `13800000001` | 只能查看并申诉自己宿舍违规 |
| 学生扩展权限示例 | `26050540102` | `13800000002` | 已拥有学院查看、学院录入权限 |
| 待绑定学生 | `26050540201` | 未绑定 | 首次登录后需要绑定手机号和宿舍 |
| 班级教师 | `90000000001` | `13900000001` | 默认只能看自己班学生违规，无审核权 |
| 校级教师 | `90000000002` | `13900000002` | 已拥有全校查看、全校审核、全校学生授权权限 |

## 学号规则

学生账号不开放自助注册，由教师导入学号和姓名生成。导入后初始密码为 `123456`。

学号固定为 11 位数字，系统会拆分并保存各段信息：

| 位置 | 含义 | 示例 `26050540101` |
| --- | --- | --- |
| 1-2 位 | 入学年份 | `26` |
| 3-4 位 | 学院代码 | `05` |
| 5-6 位 | 院内专业代码 | `05` |
| 7 位 | 就读年数 | `4` |
| 8-9 位 | 班级代码 | `01` |
| 10-11 位 | 班内学号 | `01` |

数据库中 `student_info` 会保存 `college_id`、`major_code`、`study_years`、`class_code`、`serial_no`，并用约束保证这些字段与学号内容一致。

## 权限模型

### 学生基础能力

普通学生不需要额外权限即可：

- 查看自己绑定宿舍的违规记录
- 对自己宿舍的违规记录提交申诉

### 学生可授予权限

| 权限码 | 说明 |
| --- | --- |
| `STUDENT_VIEW_DORM` | 查看自己寝室违规情况 |
| `STUDENT_VIEW_COLLEGE` | 查看自己教学院违规情况 |
| `STUDENT_VIEW_SCHOOL` | 查看全校寝室违规情况 |
| `STUDENT_SUBMIT_COLLEGE` | 提交自己教学院内的违规记录 |
| `STUDENT_SUBMIT_SCHOOL` | 提交全校范围内的违规记录 |
| `STUDENT_AUDIT_COLLEGE` | 审核自己教学院内的违规申诉 |
| `STUDENT_AUDIT_SCHOOL` | 审核全校违规申诉 |

### 教师基础能力

教师账号默认可以：

- 查看自己班级学生所在宿舍的违规情况
- 查看自己班级学生列表
- 导入自己班级学生账号
- 修改自己班级学生宿舍成员情况

基础教师默认没有审核权，也不能授权学生权限。

### 教师可授予权限

| 权限码 | 说明 |
| --- | --- |
| `TEACHER_VIEW_COLLEGE` | 查看自己教学院的违规情况 |
| `TEACHER_VIEW_SCHOOL` | 查看全校违规情况 |
| `TEACHER_AUDIT_COLLEGE` | 审核自己教学院违规申诉 |
| `TEACHER_AUDIT_SCHOOL` | 审核全校违规申诉 |
| `TEACHER_GRANT_STUDENT_COLLEGE` | 赋予学生院级权限 |
| `TEACHER_GRANT_STUDENT_SCHOOL` | 赋予学生校级权限 |

院级授权教师只能给学生授予院级或寝室级学生权限；校级授权教师可以授予全部学生权限。

## 页面使用说明

### 登录

打开 `/SAMS/` 后，输入 11 位学号、教师工号或已绑定手机号登录。账号类型可以选择“自动识别”，也可以手动选择学生或教师。

勾选“一周内免登录”后，系统会写入 `SAMS_REMEMBER` HttpOnly Cookie，服务端只保存令牌摘要。

### 学生首次绑定

教师导入的新学生首次登录后会进入绑定页面，需要填写：

- 手机号
- 楼栋号，例如 `01`
- 房间号，例如 `101`

绑定完成后才能进入主界面。后续可以用学号或手机号登录。

### 违规记录

所有已登录用户都能进入“违规记录”页，但可见范围由权限决定：

- 普通学生：自己的宿舍
- 有寝室/学院/全校查看权限的学生：对应范围
- 基础教师：自己班学生所在宿舍
- 有学院/全校查看权限的教师：对应范围

支持按学院、楼栋、房间筛选，但筛选不会扩大当前账号权限范围。

### 违规录入

拥有录入权限的账号可以进入“违规录入”页：

- 学生需要 `STUDENT_SUBMIT_COLLEGE` 或 `STUDENT_SUBMIT_SCHOOL`
- 教师需要具备学院或全校查看范围，基础班级教师默认不能录入

录入时需要填写学院、楼栋、房间、违规类型、说明和发生时间。宿舍必须已经存在于 `dorm_info`。

### Excel 批量导入违规

“违规录入”页支持上传 `.xlsx` 或 `.xls`。第一行为表头，从第二行开始读取，列顺序固定：

| 列 | 内容 | 示例 |
| --- | --- | --- |
| A | 学院编号 | `05` |
| B | 楼栋号 | `01` |
| C | 房间号 | `101` |
| D | 违规类型 | `卫生不合格` |
| E | 情况说明 | `地面未清扫` |
| F | 发生时间 | `2026-06-10 09:30` |

任意一行不符合权限范围、宿舍归属或字段格式时，导入会失败并返回错误信息。

### 申诉与审核

学生可以对自己宿舍的违规记录提交申诉。同一条违规记录只能存在一个待审核申诉。

拥有审核权限的账号可以进入“申诉审核”页：

- `STUDENT_AUDIT_COLLEGE` / `TEACHER_AUDIT_COLLEGE`：审核本学院申诉
- `STUDENT_AUDIT_SCHOOL` / `TEACHER_AUDIT_SCHOOL`：审核全校申诉

审核结果只能是通过或驳回，并必须填写审核意见。

### 学生与宿舍

教师可以进入“学生与宿舍”页：

- 基础教师只能看到自己班级学生
- 学院权限教师可以看到本学院学生
- 全校权限教师可以看到全校学生

教师可以在列表中直接修改学生楼栋号和房间号。若宿舍不存在，系统会自动在当前学生所属学院下创建对应宿舍。

### 导入学生账号

在“学生与宿舍”页右侧文本框中，每行输入：

```text
学号,姓名
```

示例：

```text
26050540103,张三
26050540104,李四
```

导入规则：

- 学号必须是 11 位数字
- 系统自动解析学院、专业、年制、班级、序号
- 基础教师只能导入自己班级学生
- 学院权限教师只能导入本学院学生
- 全校权限教师可以导入全校学生
- 初始密码固定为 `123456`
- 新学生首次登录需要绑定手机号和宿舍

### 学生授权

拥有学生授权权限的教师可以进入“学生授权”页，为学生勾选模块化权限并保存。

授权边界：

- `TEACHER_GRANT_STUDENT_COLLEGE`：只能管理本学院学生，只能授予寝室级/院级学生权限
- `TEACHER_GRANT_STUDENT_SCHOOL`：可以管理全校学生，可以授予全部学生权限

## 常用 API

默认部署地址：

```text
http://localhost:8080/SAMS
```

常用接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/auth/login` | 登录 |
| `GET` | `/auth/current` | 获取当前登录用户 |
| `GET` | `/auth/logout` | 退出登录 |
| `GET` | `/global/colleges` | 查询可见学院 |
| `GET` | `/global/dorms` | 查询可见宿舍 |
| `GET` | `/violation/list` | 查询违规记录 |
| `POST` | `/violation/add` | 新增违规记录 |
| `POST` | `/violation/importExcel` | Excel 导入违规记录 |
| `GET` | `/violation/exportWeek` | 导出本周违规汇总 |
| `POST` | `/violation/appeal` | 学生提交申诉 |
| `GET` | `/violation/appealList` | 查询待审核/历史申诉 |
| `POST` | `/violation/audit` | 审核申诉 |
| `GET` | `/student/list` | 查询学生列表 |
| `POST` | `/student/importStudents` | 教师导入学生 |
| `POST` | `/student/bindProfile` | 学生首次绑定手机号和宿舍 |
| `POST` | `/student/updateDorm` | 教师修改学生宿舍 |
| `GET` | `/student/permissionOptions` | 查询可授予学生权限 |
| `POST` | `/student/updatePermissions` | 更新学生权限 |

更细的接口字段可以参考 [API.md](API.md)。如果 API 文档与代码不一致，以当前代码为准。

## curl 简易测试流程

登录校级教师：

```bash
curl -i -c cookies.txt ^
  -H "Content-Type: application/json" ^
  -d "{\"account\":\"90000000002\",\"password\":\"123456\",\"account_type\":\"teacher\",\"remember\":true}" ^
  http://localhost:8080/SAMS/auth/login
```

查询当前用户：

```bash
curl -b cookies.txt http://localhost:8080/SAMS/auth/current
```

导入学生：

```bash
curl -b cookies.txt ^
  -H "Content-Type: application/json" ^
  -d "{\"students\":[{\"student_id\":\"26050540103\",\"name\":\"张三\"}]}" ^
  http://localhost:8080/SAMS/student/importStudents
```

录入违规：

```bash
curl -b cookies.txt ^
  -H "Content-Type: application/json" ^
  -d "{\"college_id\":\"05\",\"building_no\":\"01\",\"room_no\":\"101\",\"violation_type\":\"卫生不合格\",\"description\":\"地面未清扫\",\"occurred_at\":\"2026-06-10 09:30\"}" ^
  http://localhost:8080/SAMS/violation/add
```

查询违规：

```bash
curl -b cookies.txt http://localhost:8080/SAMS/violation/list
```

## 常见问题

### 1. 登录后看不到某个菜单

菜单由当前账号的 `permissions` 决定。基础学生只看到违规记录，基础教师只看到违规记录和学生与宿舍。需要先给账号配置对应权限。

### 2. 学生登录后要求绑定资料

这是正常流程。教师导入学生时不会预先登记手机号和宿舍，新学生首次登录必须绑定。

### 3. 导入学生失败

优先检查：

- 学号是否 11 位数字
- 学号第 3-4 位学院代码是否存在或是否在当前教师权限范围内
- 基础教师导入的学生班级代码是否等于教师自己的 `class_code`
- 姓名是否为空

### 4. 录入违规失败

优先检查：

- 当前账号是否有录入权限
- 学院是否在当前账号权限范围内
- 楼栋号是否 2 位数字
- 房间号是否 3 位数字
- `dorm_info` 中是否存在该学院下的宿舍

### 5. 旧数据库如何升级

当前 [database/schema.sql](database/schema.sql) 是新结构完整建表脚本。旧库中存在 `role=cadre`、`college_teacher`、`school_teacher` 等旧角色时，建议备份数据后重建库；如果要保留旧数据，需要手动迁移到 `student_permission` 和 `teacher_permission`。

## 项目架构

```text
SAMS
├── database
│   └── schema.sql                  数据库建表、权限字典、演示数据
├── src
│   ├── pom.xml                     Maven 工程配置，打包为 WAR
│   ├── main
│   │   ├── java
│   │   │   ├── base                JFinal 配置和基础 Controller
│   │   │   ├── controller          认证、全局数据、学生、违规记录等接口
│   │   │   ├── intercept           登录校验和异常处理拦截器
│   │   │   ├── model               ActiveRecord Model 映射类
│   │   │   └── service             登录、密码、权限范围等业务服务
│   │   └── resources               数据库连接、日志等配置
│   └── test                        JUnit 测试
├── web
│   ├── index.html                  Vue 单页应用入口
│   ├── assets
│   │   ├── app.js                  前端状态、请求、页面逻辑
│   │   ├── app.css                 页面样式
│   │   └── vue.global.prod.js      本地 Vue 3 运行时
│   └── WEB-INF/web.xml             Web 应用配置
└── API.md                          接口说明文档
```

后端采用 JFinal MVC：

- `BaseConfig` 注册路由、数据库连接和 ActiveRecord 映射
- `AuthInterceptor` 负责登录态恢复与接口保护
- `ApiExceptionInterceptor` 统一把业务异常转换为 JSON 响应
- `AuthService` 负责登录、免登录令牌、当前用户信息和权限加载
- `AccessService` 负责模块化权限判断和数据范围计算
- `StudentViolationController` 负责违规记录、导入导出、申诉和审核
- `StudentController` 负责学生列表、学生导入、首次绑定、宿舍成员维护和学生授权

前端是无构建步骤的 Vue 3 单页应用：

- 页面直接加载 `vue.global.prod.js`
- `app.js` 使用 `fetch` 调用 JFinal JSON 接口
- 菜单、按钮和页面内容根据当前用户 `permissions` 动态显示
- 登录态由服务端 Session 和 `SAMS_REMEMBER` Cookie 共同维护
