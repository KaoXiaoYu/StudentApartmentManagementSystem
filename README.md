# StudentApartmentManagementSystem
just for better apartment management




## 项目结构
***(当前版本未归档)***\
项目根目录\
├── src (Java源代码)\
│&emsp;└── main\
│&emsp;│&emsp;&emsp;└── java\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── controller\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── exception\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── intercept\
│&emsp;│&emsp;&emsp;&emsp;&emsp;├── model\
│&emsp;│&emsp;&emsp;&emsp;&emsp;└── service\
│&emsp;└── resources\
└──web\
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

### *用户登出*
    Get /user/logout

### *违规查询*
    POST /violation/recentList

JSON格式

    {
    "unit_id": "your_unit",
    "room_id": "your_room",
    "time": "your_time"
    }
### *违规申诉*
    POST /violation/appeal

JSON格式

    {
    "violation_id": "violation_id",
    "appeal_content": "我说我要申诉，你尔朵龙吗",
    "evidence_url": [
    "url.com"
    ]
    }


## *数据库*
### *student_info*
student_id char(11) key\
phone_number char(11)\
unit_id char(2)\
room_id char(3)\
permission int

### *violation_info*
violation_id char(8)\
unit_id char(2)\
room_id char(3)\
violation_code char(2)\
date_time datetime\
handle_status varchar(20)\
appeal_status varchar(20)\
appeal_content text\
audit_time datetime\
audit_reply text\