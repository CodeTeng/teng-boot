-- =============================================
-- RBAC 权限体系 + 文件管理 建表脚本
-- =============================================

-- 1. 用户表（基于原有结构，删除微信字段）
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            bigint                                  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `username`      varchar(256)                            NOT NULL COMMENT '用户账号',
    `user_password` varchar(512)                            NOT NULL COMMENT '用户密码',
    `user_role`     varchar(100)  DEFAULT 'user'            NOT NULL COMMENT '用户角色 user/admin/ban',
    `user_phone`    varchar(11)   DEFAULT NULL COMMENT '用户手机号',
    `user_real_name` varchar(60)  DEFAULT NULL COMMENT '用户真实姓名',
    `user_gender`   tinyint       DEFAULT 0                 NOT NULL COMMENT '性别：0-男性，1-女性',
    `user_age`      tinyint unsigned DEFAULT NULL COMMENT '用户年龄',
    `user_email`    varchar(50)   DEFAULT NULL COMMENT '用户邮箱',
    `user_avatar`   varchar(1024) DEFAULT NULL COMMENT '用户头像',
    `user_birthday` date          DEFAULT NULL COMMENT '用户生日',
    `user_profile`  varchar(512)  DEFAULT NULL COMMENT '用户简介',
    `status`        tinyint       DEFAULT 1                 NOT NULL COMMENT '账户状态：0-禁用 1-正常',
    `create_time`   datetime      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   datetime      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater`       bigint        DEFAULT NULL COMMENT '创建者id',
    `updater`       bigint        DEFAULT 0                 NULL COMMENT '更新者id',
    `is_delete`     tinyint       DEFAULT 0                 NOT NULL COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 2. 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          bigint                               NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(30)                          NOT NULL COMMENT '角色名称',
    `role_key`    varchar(100)                         NOT NULL COMMENT '角色权限字符串',
    `role_sort`   int         DEFAULT 0                NOT NULL COMMENT '显示顺序',
    `status`      tinyint     DEFAULT 1                NOT NULL COMMENT '状态 0-停用 1-正常',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater`     bigint      DEFAULT NULL COMMENT '创建者id',
    `updater`     bigint      DEFAULT NULL COMMENT '更新者id',
    `is_delete`   tinyint     DEFAULT 0                NOT NULL COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '角色表';

-- 3. 菜单权限表
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`          bigint                               NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(50)                          NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint      DEFAULT 0                NOT NULL COMMENT '父菜单ID',
    `order_num`   int         DEFAULT 0                NOT NULL COMMENT '显示顺序',
    `path`        varchar(200) DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) DEFAULT NULL COMMENT '组件路径',
    `query`       varchar(255) DEFAULT NULL COMMENT '路由参数',
    `menu_type`   char(1)     DEFAULT '' COMMENT '菜单类型 M-目录 C-菜单 F-按钮',
    `visible`     tinyint     DEFAULT 1                NOT NULL COMMENT '显示状态 0-隐藏 1-显示',
    `status`      tinyint     DEFAULT 1                NOT NULL COMMENT '菜单状态 0-停用 1-正常',
    `perms`       varchar(100) DEFAULT NULL COMMENT '权限标识 如 system:user:list',
    `icon`        varchar(100) DEFAULT '#' COMMENT '菜单图标',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `creater`     bigint      DEFAULT NULL COMMENT '创建者id',
    `updater`     bigint      DEFAULT NULL COMMENT '更新者id',
    `is_delete`   tinyint     DEFAULT 0                NOT NULL COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表';

-- 4. 用户角色关联表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表';

-- 5. 角色菜单关联表
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表';

-- 6. 文件记录表
DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record`
(
    `id`          bigint                               NOT NULL AUTO_INCREMENT COMMENT '文件id',
    `file_name`   varchar(255)                         NOT NULL COMMENT '文件名',
    `file_size`   bigint      DEFAULT NULL COMMENT '文件大小(字节)',
    `file_type`   varchar(50) DEFAULT NULL COMMENT '文件类型',
    `file_url`    varchar(1024)                        NOT NULL COMMENT '文件访问地址',
    `file_key`    varchar(500) DEFAULT NULL COMMENT '文件存储key(COS key)',
    `biz_type`    varchar(50) DEFAULT NULL COMMENT '业务类型 user_avatar等',
    `user_id`     bigint      DEFAULT NULL COMMENT '上传用户id',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `is_delete`   tinyint     DEFAULT 0                NOT NULL COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '文件记录表';

-- 7. 系统日志表（保留原有结构）
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`          bigint                               NOT NULL COMMENT '主键id',
    `user_id`     bigint                               NOT NULL COMMENT '用户id',
    `username`    varchar(256) DEFAULT NULL COMMENT '用户名',
    `value`       varchar(256) DEFAULT NULL COMMENT '用户操作描述',
    `operation`   varchar(50)  DEFAULT NULL COMMENT '请求方式:GET POST PUT DELETE',
    `cost_time`   bigint       DEFAULT NULL COMMENT '响应时间,单位毫秒',
    `url`         varchar(256) DEFAULT NULL COMMENT '请求路径',
    `method_name` varchar(256) DEFAULT NULL COMMENT '请求方法（控制层方法全限定名）',
    `params`      varchar(1024) DEFAULT NULL COMMENT '请求参数',
    `ip`          varchar(64)  DEFAULT NULL COMMENT 'IP地址',
    `os`          varchar(256)  DEFAULT NULL COMMENT '操作系统',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `is_delete`   tinyint      DEFAULT 0                NOT NULL COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '系统日志表';

-- =============================================
-- 初始数据
-- =============================================

-- 默认管理员角色
INSERT INTO `role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, 1, '拥有所有权限'),
(2, '普通用户',   'user',  2, 1, '普通用户权限');

-- 默认菜单（系统管理）
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
-- 一级目录
(1,  '系统管理',  0, 1, '/system',       NULL,                    'M', 1, 1, NULL,              'Setting'),
-- 用户管理
(2,  '用户管理',  1, 1, 'user',          'system/user/index.vue',  'C', 1, 1, 'system:user:list', 'User'),
(3,  '用户新增',  2, 1, NULL,            NULL,                     'F', 1, 1, 'system:user:add',  NULL),
(4,  '用户编辑',  2, 1, NULL,            NULL,                     'F', 1, 1, 'system:user:edit', NULL),
(5,  '用户删除',  2, 1, NULL,            NULL,                     'F', 1, 1, 'system:user:delete', NULL),
-- 角色管理
(6,  '角色管理',  1, 2, 'role',          'system/role/index.vue',  'C', 1, 1, 'system:role:list', 'UserFilled'),
(7,  '角色新增',  6, 1, NULL,            NULL,                     'F', 1, 1, 'system:role:add',  NULL),
(8,  '角色编辑',  6, 1, NULL,            NULL,                     'F', 1, 1, 'system:role:edit', NULL),
(9,  '角色删除',  6, 1, NULL,            NULL,                     'F', 1, 1, 'system:role:delete', NULL),
-- 菜单管理
(10, '菜单管理',  1, 3, 'menu',          'system/menu/index.vue',  'C', 1, 1, 'system:menu:list', 'Menu'),
(11, '菜单新增',  10, 1, NULL,           NULL,                     'F', 1, 1, 'system:menu:add',  NULL),
(12, '菜单编辑',  10, 1, NULL,           NULL,                     'F', 1, 1, 'system:menu:edit', NULL),
(13, '菜单删除',  10, 1, NULL,           NULL,                     'F', 1, 1, 'system:menu:delete', NULL),
-- 一级目录
(14, '系统监控',  0, 2, '/monitor',      NULL,                    'M', 1, 1, NULL,              'Monitor'),
-- 操作日志
(15, '操作日志',  14, 1, 'log',          'monitor/log/index.vue',  'C', 1, 1, 'monitor:log:list', 'Document'),
(16, '日志删除',  15, 1, NULL,           NULL,                     'F', 1, 1, 'monitor:log:delete', NULL),
-- 一级目录
(17, '文件管理',  0, 3, '/file',         NULL,                    'M', 1, 1, NULL,              'Folder'),
-- 文件管理
(18, '文件列表',  17, 1, 'list',         'file/list/index.vue',    'C', 1, 1, 'file:list',       'Files');

-- 角色菜单关联（admin 拥有所有菜单）
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`;

-- 角色菜单关联（普通用户基础菜单）
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES
(2, 17),  -- 文件管理目录
(2, 18);  -- 文件列表

-- 默认管理员用户（密码为 123456 的 BCrypt 密文，盐值由应用层生成后替换）
-- 实际密码密文请在应用启动后通过注册或数据库直接更新
