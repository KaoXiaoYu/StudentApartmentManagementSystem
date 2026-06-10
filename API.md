# SAMS 后端 API 文档

本文档对应当前 JFinal 后端实现，可用于 Postman、Apifox 或 curl 测试。

## 1. 基本信息

- 本地基础地址：`http://localhost:8080/SAMS`
- 普通请求格式：`application/json`
- Excel 上传格式：`multipart/form-data`
- 字符编码：`UTF-8`
- 登录状态：Cookie Session
- 一周免登录 Cookie：`SAMS_REMEMBER`

除登录接口外，其他接口应保留登录响应中的 Cookie。使用 Postman 或 Apifox 时通常会自动保存 Cookie。

### 通用成功响应

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

部分没有返回数据的接口只包含 `code` 和 `msg`。

### 通用失败响应

```json
{
  "code": 400,
  "msg": "具体错误信息"
}
```

常见 HTTP 状态码：

| 状态码 | 含义 |
| --- | --- |
| `200` | 请求成功 |
| `400` | 参数或业务规则错误 |
| `401` | 未登录、登录失效或账号密码错误 |
| `403` | 当前账号无权执行操作 |
| `404` | 指定数据不存在 |
| `500` | 服务器处理失败 |

## 2. 角色和数据范围

| `user_type` | `role` | 说明 |
| --- | --- | --- |
| `student` | `student` | 基础学生，只能查询和申诉本人宿舍违规 |
| `student` | `cadre` | 学生干部，只能管理本教学院宿舍 |
| `teacher` | `college_teacher` | 学院教师，只能管理本教学院宿舍和学生 |
| `teacher` | `school_teacher` | 校级教师，可以管理全部教学院 |

演示账号的密码均为 `123456`：

| 角色 | 账号 | 手机号 |
| --- | --- | --- |
| 基础学生 | `20260000001` | `13800000001` |
| 学生干部 | `20260000002` | `13800000002` |
| 学院教师 | `90000000001` | `13900000001` |
| 校级教师 | `90000000002` | `13900000002` |

## 3. 认证接口

### 3.1 登录

`POST /auth/login`

兼容地址：`POST /user/login`

请求体：

```json
{
  "account": "20260000001",
  "password": "123456",
  "account_type": "student",
  "remember": true
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `account` | 是 | 11 位学号、工号或绑定手机号 |
| `password` | 是 | 长度为 3 至 64 位 |
| `account_type` | 否 | `student`、`teacher` 或空字符串；空字符串时自动识别 |
| `remember` | 否 | `true` 时签发有效期 7 天的免登录 Cookie |

学生登录响应示例：

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "user_id": "20260000001",
    "student_id": "20260000001",
    "phone_number": "13800000001",
    "name": "基础学生示例",
    "role": "student",
    "college_id": "CS",
    "building_no": "01",
    "room_no": "101",
    "user_type": "student"
  }
}
```

教师登录响应不包含 `building_no` 和 `room_no`。

当 `remember=true` 时，响应会同时设置：

- `JSESSIONID`：当前 Session
- `SAMS_REMEMBER`：7 天免登录令牌，具有 `HttpOnly` 属性

### 3.2 获取当前用户

`GET /auth/current`

兼容地址：`GET /user/current`

权限：已登录用户。

响应示例：

```json
{
  "code": 200,
  "msg": "已登录",
  "data": {
    "user_id": "20260000001",
    "user_type": "student",
    "name": "基础学生示例",
    "role": "student",
    "college_id": "CS",
    "building_no": "01",
    "room_no": "101"
  }
}
```

如果 Session 已失效但存在有效的 `SAMS_REMEMBER` Cookie，后端会自动恢复登录状态。

### 3.3 退出登录

`GET /auth/logout`

兼容地址：`GET /user/logout`

作用：

- 删除当前服务端免登录令牌
- 清除 `SAMS_REMEMBER` Cookie
- 销毁当前 Session

响应：

```json
{
  "code": 200,
  "msg": "已退出登录"
}
```

## 4. 教学院和宿舍

### 4.1 查询可管理的教学院

`GET /global/colleges`

权限：已登录用户。

数据范围：

- 校级教师返回全部教学院。
- 其他账号只返回本人所属教学院。

响应示例：

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "college_id": "CS",
      "college_name": "计算机学院"
    }
  ]
}
```

### 4.2 查询可管理的宿舍

`GET /global/dorms`

权限：已登录用户。

可选 Query 参数：

| 参数 | 说明 |
| --- | --- |
| `college_id` | 校级教师可按教学院筛选；其他角色忽略该参数 |

示例：

```text
GET /global/dorms?college_id=CS
```

响应示例：

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "college_id": "CS",
      "building_no": "01",
      "room_no": "101"
    }
  ]
}
```

## 5. 违规记录

### 5.1 查询违规记录

`GET /violation/list`

兼容地址：`GET /violation/recentList`

权限：所有已登录用户。

可选 Query 参数：

| 参数 | 说明 |
| --- | --- |
| `college_id` | 仅校级教师可用于筛选教学院 |
| `building_no` | 2 位数字；与 `room_no` 一起使用 |
| `room_no` | 3 位数字；与 `building_no` 一起使用 |

数据范围：

- 基础学生只能查询本人绑定宿舍，Query 参数不会扩大范围。
- 学生干部和学院教师只能查询本教学院。
- 校级教师默认查询全校，也可以按教学院、宿舍筛选。

示例：

```text
GET /violation/list?college_id=CS&building_no=01&room_no=101
```

响应示例：

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "violation_id": "32位记录编号",
      "college_id": "CS",
      "college_name": "计算机学院",
      "building_no": "01",
      "room_no": "101",
      "violation_type": "卫生不合格",
      "description": "地面未清扫",
      "occurred_at": "2026-06-10 09:30:00",
      "created_by_name": "学生干部示例",
      "created_at": "2026-06-10 10:00:00",
      "appeal_status": null
    }
  ]
}
```

`appeal_status` 可能值：

- `null`：未申诉
- `pending`：待审核
- `approved`：申诉通过
- `rejected`：申诉驳回

### 5.2 在线录入违规记录

`POST /violation/add`

权限：学生干部、学院教师、校级教师。

请求体：

```json
{
  "college_id": "CS",
  "building_no": "01",
  "room_no": "101",
  "violation_type": "卫生不合格",
  "description": "地面未清扫，垃圾未及时处理",
  "occurred_at": "2026-06-10 09:30"
}
```

字段规则：

| 字段 | 必填 | 规则 |
| --- | --- | --- |
| `college_id` | 是 | 必须是现有教学院，且在当前账号管理范围内 |
| `building_no` | 是 | 2 位数字 |
| `room_no` | 是 | 3 位数字 |
| `violation_type` | 是 | 最长 50 字 |
| `description` | 是 | 最长 1000 字 |
| `occurred_at` | 否 | `yyyy-MM-dd HH:mm` 或 `yyyy-MM-dd`；为空时使用当前时间 |

指定宿舍必须已存在于 `dorm_info`，并且属于所选教学院。

### 5.3 Excel 批量导入

`POST /violation/importExcel`

权限：学生干部、学院教师、校级教师。

Content-Type：`multipart/form-data`

表单字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `file` | File | `.xlsx` 或 `.xls` 文件 |

工作表第一行为表头，从第二行开始按固定列顺序读取：

| 列 | 内容 | 示例 |
| --- | --- | --- |
| A | 教学院编号 | `CS` |
| B | 楼栋号 | `01` |
| C | 房间号 | `101` |
| D | 违规类型 | `卫生不合格` |
| E | 情况说明 | `地面未清扫` |
| F | 发生时间 | `2026-06-10 09:30` |

导入操作仍受学院权限和宿舍归属限制。任意一行校验失败时接口返回错误。

成功响应示例：

```json
{
  "code": 200,
  "msg": "成功导入 3 条违规记录"
}
```

### 5.4 导出本周违规汇总

`GET /violation/exportWeek`

权限：学生干部、学院教师、校级教师。

响应：Excel 文件下载。

统计周期：服务器当前日期所在周的星期一 `00:00:00` 至下周一 `00:00:00`。

数据范围：

- 学生干部和学院教师导出本教学院数据。
- 校级教师导出全校数据。

## 6. 申诉

### 6.1 提交申诉

`POST /violation/appeal`

权限：学生账号，包括基础学生和学生干部。

限制：

- 只能申诉本人绑定宿舍的违规记录。
- 同一违规记录不能重复提交状态为 `pending` 的申诉。
- 申诉理由长度为 5 至 1000 字。

请求体：

```json
{
  "violation_id": "32位违规记录编号",
  "reason": "检查当时宿舍正在进行统一清扫，希望重新核实。"
}
```

响应：

```json
{
  "code": 200,
  "msg": "申诉已提交"
}
```

### 6.2 查询申诉列表

`GET /violation/appealList`

权限：学生干部、学院教师、校级教师。

数据范围：

- 学生干部和学院教师只能查询本教学院。
- 校级教师可以查询全部教学院。

响应示例：

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "appeal_id": "32位申诉编号",
      "violation_id": "32位违规编号",
      "student_id": "20260000001",
      "student_name": "基础学生示例",
      "reason": "希望重新核实",
      "status": "pending",
      "audit_reply": null,
      "created_at": "2026-06-10 12:00:00",
      "audited_at": null,
      "college_id": "CS",
      "building_no": "01",
      "room_no": "101",
      "violation_type": "卫生不合格",
      "description": "地面未清扫",
      "occurred_at": "2026-06-10 09:30:00"
    }
  ]
}
```

### 6.3 审核申诉

`POST /violation/audit`

权限：学生干部、学院教师、校级教师。

请求体：

```json
{
  "appeal_id": "32位申诉编号",
  "status": "approved",
  "reply": "复核后确认申诉情况属实。"
}
```

字段说明：

| 字段 | 规则 |
| --- | --- |
| `appeal_id` | 必须是存在且尚未审核的申诉 |
| `status` | 只能是 `approved` 或 `rejected` |
| `reply` | 必填，审核意见 |

只有处于 `pending` 状态的申诉可以审核，并且审核人必须有该教学院的数据权限。

## 7. 学生权限管理

### 7.1 查询学生列表

`GET /student/list`

权限：学院教师、校级教师。

校级教师可选 Query 参数：

| 参数 | 说明 |
| --- | --- |
| `college_id` | 按教学院筛选；不传时查询全部学生 |

学院教师始终只能查询本教学院。

响应示例：

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "student_id": "20260000001",
      "phone_number": "13800000001",
      "name": "基础学生示例",
      "role": "student",
      "college_id": "CS",
      "building_no": "01",
      "room_no": "101"
    }
  ]
}
```

### 7.2 授权或撤销学生干部

`POST /student/authorize`

权限：学院教师、校级教师。

请求体：

```json
{
  "student_id": "20260000001",
  "authorized": true
}
```

| `authorized` | 结果 |
| --- | --- |
| `true` | 将角色改为 `cadre` |
| `false` | 将角色改为 `student` |

学院教师只能修改本教学院学生，校级教师可以修改任意教学院学生。

## 8. curl 测试流程

以下命令在项目部署到 `/SAMS` 后执行。`cookies.txt` 用于保存登录 Cookie。

### 8.1 登录并保存 Cookie

```bash
curl -i -c cookies.txt \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"20260000002\",\"password\":\"123456\",\"account_type\":\"student\",\"remember\":true}" \
  http://localhost:8080/SAMS/auth/login
```

### 8.2 携带 Cookie 查询当前用户

```bash
curl -b cookies.txt http://localhost:8080/SAMS/auth/current
```

### 8.3 学生干部录入违规

```bash
curl -b cookies.txt \
  -H "Content-Type: application/json" \
  -d "{\"college_id\":\"CS\",\"building_no\":\"01\",\"room_no\":\"101\",\"violation_type\":\"卫生不合格\",\"description\":\"地面未清扫\",\"occurred_at\":\"2026-06-10 09:30\"}" \
  http://localhost:8080/SAMS/violation/add
```

### 8.4 查询违规并取得 `violation_id`

```bash
curl -b cookies.txt http://localhost:8080/SAMS/violation/list
```

### 8.5 上传 Excel

```bash
curl -b cookies.txt \
  -F "file=@violation.xlsx" \
  http://localhost:8080/SAMS/violation/importExcel
```

### 8.6 下载本周汇总

```bash
curl -b cookies.txt \
  -o weekly.xlsx \
  http://localhost:8080/SAMS/violation/exportWeek
```

### 8.7 退出

```bash
curl -b cookies.txt http://localhost:8080/SAMS/auth/logout
```

## 9. 推荐测试顺序

1. 使用学生干部账号登录。
2. 调用 `/global/colleges` 和 `/global/dorms` 检查基础数据。
3. 调用 `/violation/add` 为 `CS-01-101` 添加一条违规。
4. 退出并使用基础学生账号登录。
5. 调用 `/violation/list` 取得违规编号。
6. 调用 `/violation/appeal` 提交申诉。
7. 退出并重新使用学生干部账号登录。
8. 调用 `/violation/appealList` 取得申诉编号。
9. 调用 `/violation/audit` 完成审核。
10. 使用学院教师测试 `/student/list` 和 `/student/authorize`。
11. 使用校级教师测试跨学院查询和全校汇总导出。
