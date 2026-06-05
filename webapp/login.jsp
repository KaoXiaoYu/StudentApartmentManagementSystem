<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户登录</title>
    <style>
        /* 简单的居中样式，让页面更美观 */
        .login-container {
            width: 300px;
            margin: 100px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover { background-color: #45a049; }
    </style>
</head>
<body>
<div class="login-container">
    <h2 style="text-align: center;">系统登录</h2>

    <!-- 核心表单部分 -->
    <form action="login" method="post">
        <div class="form-group">
            <label for="username">账号：</label>
            <input type="text" id="username" name="username" placeholder="请输入账号" required>
        </div>

        <div class="form-group">
            <label for="password">密码：</label>
            <input type="password" id="password" name="password" placeholder="请输入密码" required>
        </div>

        <button type="submit">登 录</button>
    </form>
</div>
</body>
</html>