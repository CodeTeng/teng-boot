-- =============================================
-- 修复唯一索引问题（允许逻辑删除后重新添加相同标识）
-- 问题：之前添加的角色/用户然后删除了，再次添加相同的标识时会提示"系统内部错误"
-- 原因：唯一索引会对所有记录（包括已逻辑删除的 is_delete=1 的记录）生效
-- 解决：创建部分唯一索引，只对未删除的记录生效
-- =============================================

-- ---------------------------------------------
-- 修复 user 表的 uk_username 唯一索引
-- ---------------------------------------------
-- 1. 删除旧的唯一索引
ALTER TABLE `user` DROP INDEX `uk_username`;

-- 2. 创建新的部分唯一索引（仅对 is_delete = 0 的记录生效）
-- 注意：此语法仅支持 MySQL 8.0+
CREATE UNIQUE INDEX `uk_username` ON `user`(`username`) WHERE `is_delete` = 0;

-- ---------------------------------------------
-- 修复 role 表的 uk_role_key 唯一索引
-- ---------------------------------------------
-- 1. 删除旧的唯一索引
ALTER TABLE `role` DROP INDEX `uk_role_key`;

-- 2. 创建新的部分唯一索引（仅对 is_delete = 0 的记录生效）
CREATE UNIQUE INDEX `uk_role_key` ON `role`(`role_key`) WHERE `is_delete` = 0;

-- ---------------------------------------------
-- 验证索引是否创建成功
-- ---------------------------------------------
-- SHOW INDEX FROM `user` WHERE Key_name = 'uk_username';
-- SHOW INDEX FROM `role` WHERE Key_name = 'uk_role_key';

ALTER TABLE sys_log ADD COLUMN log_type INT DEFAULT 1 COMMENT '日志类型: 1-操作日志 2-登录日志 3-退出日志';

-- 更新现有数据为操作日志
UPDATE sys_log SET log_type = 1 WHERE log_type IS NULL;

-- 添加索引以提高查询性能
ALTER TABLE sys_log ADD INDEX idx_log_type (log_type);