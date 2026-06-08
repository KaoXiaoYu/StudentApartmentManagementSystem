# StudentApartmentManagementSystem
just for better apartment management



## 项目结构
***(当前版本未归档)***\
项目根目录\
├── src (Java源代码)\
│&emsp;└── main\
│&emsp;│&emsp;&emsp;└── java\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── controller\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── interface\
│&emsp;│&emsp;&emsp;&emsp;&emsp;└── service\
│&emsp;└── resources\
└──webapp\
&emsp;&emsp;└── WEB-INF

## 接口

### *用户注册*
    POST /user/register

JSON格式

        {
        "phone_number": "phone_number",
        "student_id": "student_id"
        "password": "114514",
        "unit_id": "unit_id",
        "room_id": "room_id"
        }


### *用户登录*
    POST /user/login

JSON格式

        {
        "username": "Username",
        "is_phone_number": "false",
        "password": "Password",
        "remember":"false"
        }

