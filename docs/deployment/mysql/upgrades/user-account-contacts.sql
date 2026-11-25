-- 已有开发库升级时执行一次。MySQL 8.0.13+；同租户有效账号存在重复联系方式时 ALTER 会报错。
ALTER TABLE t_user
  ALTER COLUMN password SET DEFAULT NULL,
  MODIFY COLUMN email varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci DEFAULT NULL COMMENT '邮箱',
  ALTER COLUMN mobile SET DEFAULT NULL,
  ADD UNIQUE KEY `uk_user_tenant_email` (`tenant_id`,`email`,(IF(`deleted` = 0 AND `email` <> '', 1, NULL))),
  ADD UNIQUE KEY `uk_user_tenant_mobile` (`tenant_id`,`mobile`,(IF(`deleted` = 0 AND `mobile` <> '', 1, NULL)));
