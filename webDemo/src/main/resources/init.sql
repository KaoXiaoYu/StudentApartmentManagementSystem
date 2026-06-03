-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS gps DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE gps;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    status VARCHAR(1) DEFAULT '1' COMMENT '状态：1启用；0禁用',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    status VARCHAR(1) DEFAULT '1' COMMENT '状态：1启用；0禁用',
    sort INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    query VARCHAR(255) COMMENT '路由参数',
    is_frame VARCHAR(1) DEFAULT '1' COMMENT '是否外链：1是；0否',
    menu_type VARCHAR(1) NOT NULL COMMENT '菜单类型：M目录；C菜单；F按钮',
    visible VARCHAR(1) DEFAULT '1' COMMENT '是否显示：1显示；0隐藏',
    status VARCHAR(1) DEFAULT '1' COMMENT '状态：1启用；0禁用',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 创建角色菜单关联表
CREATE TABLE IF NOT EXISTS role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 插入测试数据
-- 密码是 "123456" 的 MD5 加密值
INSERT INTO user (username, password, real_name, status, create_by) 
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '1', 'system');

INSERT INTO role (role_key, role_name, status, sort, create_by) 
VALUES ('admin', '管理员', '1', 1, 'system');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO menu (parent_id, menu_name, path, component, menu_type, visible, status, sort, create_by) VALUES
(0, '系统管理', 'system', '', 'M', '1', '1', 1, 'system'),
(1, '用户管理', 'user', 'system/user/index', 'C', '1', '1', 1, 'system'),
(1, '角色管理', 'role', 'system/role/index', 'C', '1', '1', 2, 'system'),
(1, '菜单管理', 'menu', 'system/menu/index', 'C', '1', '1', 3, 'system');

INSERT INTO role_menu (role_id, menu_id) VALUES (1, 1), (1, 2), (1, 3), (1, 4);

COMMIT;

-- 查询验证
SELECT * FROM user;
SELECT * FROM role;
SELECT * FROM menu;