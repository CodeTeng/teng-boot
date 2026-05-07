-- H2 中 USER 是关键字，设置为非关键字
SET NON_KEYWORDS USER;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(64) NOT NULL COMMENT '用户账号',
    `user_password` VARCHAR(256) NOT NULL COMMENT '用户密码',
    `user_role` VARCHAR(32) DEFAULT 'user' COMMENT '用户角色',
    `user_phone` VARCHAR(32) DEFAULT NULL COMMENT '用户手机号',
    `user_real_name` VARCHAR(64) DEFAULT NULL COMMENT '用户真实姓名',
    `user_gender` TINYINT DEFAULT NULL COMMENT '性别',
    `user_age` INT DEFAULT NULL COMMENT '用户年龄',
    `user_email` VARCHAR(128) DEFAULT NULL COMMENT '用户邮箱',
    `user_avatar` VARCHAR(1024) DEFAULT NULL COMMENT '用户头像',
    `user_birthday` DATE DEFAULT NULL COMMENT '用户生日',
    `user_profile` VARCHAR(512) DEFAULT NULL COMMENT '用户简介',
    `status` TINYINT DEFAULT 1 COMMENT '账户状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater` BIGINT DEFAULT 0 COMMENT '创建者id',
    `updater` BIGINT DEFAULT 0 COMMENT '更新者id',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除'
);

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(64) NOT NULL COMMENT '角色权限字符串',
    `role_sort` INT DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-正常',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater` BIGINT DEFAULT 0 COMMENT '创建者id',
    `updater` BIGINT DEFAULT 0 COMMENT '更新者id',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除'
);

-- 菜单权限表
CREATE TABLE IF NOT EXISTS `menu` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `path` VARCHAR(256) DEFAULT NULL COMMENT '路由地址',
    `component` VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    `query` VARCHAR(256) DEFAULT NULL COMMENT '路由参数',
    `menu_type` VARCHAR(8) DEFAULT '' COMMENT '菜单类型 M-目录 C-菜单 F-按钮',
    `visible` TINYINT DEFAULT 1 COMMENT '显示状态 0-隐藏 1-显示',
    `status` TINYINT DEFAULT 1 COMMENT '菜单状态 0-停用 1-正常',
    `perms` VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(128) DEFAULT NULL COMMENT '菜单图标',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater` BIGINT DEFAULT 0 COMMENT '创建者id',
    `updater` BIGINT DEFAULT 0 COMMENT '更新者id',
    `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除'
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
);

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
);

-- 文件记录表（避免测试时 MyMetaObjectHandler 出错）
CREATE TABLE IF NOT EXISTS `file_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `url` VARCHAR(512) DEFAULT NULL,
    `name` VARCHAR(256) DEFAULT NULL,
    `type` VARCHAR(64) DEFAULT NULL,
    `size` BIGINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `creater` BIGINT DEFAULT 0,
    `updater` BIGINT DEFAULT 0,
    `is_delete` TINYINT DEFAULT 0
);

-- 操作日志表（避免测试时 MyMetaObjectHandler 出错）
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT DEFAULT NULL,
    `username` VARCHAR(64) DEFAULT NULL,
    `value` VARCHAR(256) DEFAULT NULL,
    `operation` VARCHAR(64) DEFAULT NULL,
    `cost_time` BIGINT DEFAULT 0,
    `url` VARCHAR(512) DEFAULT NULL,
    `method_name` VARCHAR(128) DEFAULT NULL,
    `params` TEXT DEFAULT NULL,
    `ip` VARCHAR(64) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `creater` BIGINT DEFAULT 0,
    `updater` BIGINT DEFAULT 0,
    `is_delete` TINYINT DEFAULT 0
);

-- 插入测试用的初始 RBAC 数据
INSERT IGNORE INTO `role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `remark`) VALUES
(1, '普通用户', 'user', 1, 1, '默认角色'),
(2, '管理员', 'admin', 2, 1, '管理员角色');

INSERT IGNORE INTO `menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`) VALUES
(1, '用户管理', 0, 1, '/system/user', 'system/user/index', 'C', 1, 1, 'system:user:list'),
(2, '用户查询', 1, 1, '', '', 'F', 1, 1, 'system:user:query'),
(3, '用户新增', 1, 2, '', '', 'F', 1, 1, 'system:user:add'),
(4, '用户修改', 1, 3, '', '', 'F', 1, 1, 'system:user:edit'),
(5, '用户删除', 1, 4, '', '', 'F', 1, 1, 'system:user:remove');

-- 为 admin 角色分配所有菜单权限
INSERT IGNORE INTO `role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5);

-- 为 user 角色分配查询权限
INSERT IGNORE INTO `role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2);
