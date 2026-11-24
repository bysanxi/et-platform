-- ET Platform shared-schema bootstrap for an empty MySQL 8 database.
SET NAMES utf8mb4;

CREATE TABLE `c_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `tenant_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录IP',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录地点',
  `client_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录人客户端ID',
  `principal` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录人账号',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '平台',
  `engine` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '引擎类型',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '浏览器名称',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作系统',
  `login_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录类型',
  `create_by` bigint DEFAULT NULL,
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='登录日志';

CREATE TABLE `c_opt_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `tenant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '日志模块',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作描述',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作IP（支持IPv6）',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '地址',
  `trace` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '日志链路追踪id日志标志',
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类路径',
  `uri` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求地址',
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'GET' COMMENT '请求类型ENUM(''GET'', ''POST'', ''PUT'', ''DELETE'', ''PATCH'', ''TRACE'', ''HEAD'', ''OPTIONS'')',
  `request` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '请求参数',
  `response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '返回值',
  `message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '执行消息',
  `status` bit(1) DEFAULT NULL COMMENT '日志状态（true=正常;false=异常）',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `duration` bigint DEFAULT '0' COMMENT '消耗时间',
  `browser` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '浏览器名称',
  `os` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作系统',
  `engine` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '浏览器引擎',
  `platform` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '平台信息',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求令牌',
  `create_by` bigint DEFAULT '0' COMMENT '创建人id',
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统日志';

CREATE TABLE `sys_data_permission_ref` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scope_type` int DEFAULT NULL COMMENT '权限范围类型: 10-个人, 20-自定义, 30-本级, 40-本级及子级, 50-全部',
  `owner_id` bigint NOT NULL COMMENT '拥有者',
  `owner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '拥有类型（角色，用户）',
  `data_id` bigint DEFAULT NULL COMMENT '数据ID',
  `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '数据类型（机构、角色、租户等等）',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  `create_by` bigint DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT NULL COMMENT '最近修改人',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '最近修改人',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `data_perm_idx_owner_id` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2001618919068160005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限资源表';

CREATE TABLE `sys_org` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '简称',
  `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系方式',
  `parent_id` bigint DEFAULT '0' COMMENT '父ID',
  `tree_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '树节点路径',
  `sequence` int DEFAULT '1' COMMENT '排序',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `deleted` bit(1) DEFAULT b'0',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '描述',
  `create_by` bigint DEFAULT '0' COMMENT '创建人id',
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT '0' COMMENT '更新人id',
  `last_modify_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人名称',
  `last_modify_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1999051236041019395 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='组织';

INSERT INTO `sys_org` (`id`, `label`, `tenant_id`, `alias`, `tel`, `parent_id`, `tree_path`, `sequence`, `status`, `deleted`, `description`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (100, '平台团队', 1, NULL, NULL, 0, '0', 1, b'1', b'0', '初始化数据', 1, '1', '2019-07-10 17:02:18', 1, '1', '2023-11-22 08:21:42');
INSERT INTO `sys_org` (`id`, `label`, `tenant_id`, `alias`, `tel`, `parent_id`, `tree_path`, `sequence`, `status`, `deleted`, `description`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (800, '内部团队', 8, NULL, NULL, 0, '0', 1, b'1', b'0', '初始化数据', 1, '1', '2019-07-10 17:02:18', 1, '1', '2023-11-22 08:21:42');

CREATE TABLE `sys_registered_client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `client_id_issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `client_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `client_secret_expires_at` timestamp NULL DEFAULT NULL,
  `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `client_authentication_methods` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `grant_types` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `post_logout_redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `client_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `token_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `status` bit(1) DEFAULT b'1' COMMENT '0=禁用;1=启用',
  `deleted` bit(1) DEFAULT b'0',
  `create_by` bigint DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT NULL COMMENT '更新人id',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人名称',
  `last_modify_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1865050872845828099 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `sys_registered_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `client_name`, `client_authentication_methods`, `grant_types`, `redirect_uris`, `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`, `status`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, 'pc-web', '2024-09-11 06:39:32', 'pc-web', NULL, 'PC-WEB 管理系统', 'client_secret_basic', 'password', 'http://127.0.0.1:5001/oauth2/code,https://www.baidu.com', '', '[[[wp]]]', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"accessTokenTimeToLive\":120}', b'1', b'0', NULL, NULL, '2023-09-15 16:43:14', NULL, NULL, '2024-12-07 12:24:30');

CREATE TABLE `sys_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '归属的应用',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限',
  `parent_id` bigint DEFAULT '0' COMMENT '父级菜单ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组件',
  `sequence` int DEFAULT '1' COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '菜单图标',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '类型 directory=目录;menu=菜单;iframe=内嵌;link=外链;button=按钮',
  `status` bit(1) DEFAULT b'1' COMMENT '1=启用;0=禁用',
  `keep_alive` bit(1) DEFAULT NULL COMMENT '开启后页面会缓存，不会重新加载，仅在标签页启用时有效',
  `readonly` bit(1) DEFAULT b'0' COMMENT '内置菜单（0=否;1=是）',
  `shared` bit(1) DEFAULT b'0' COMMENT '公共资源（True=无需分配所有人就可以访问）',
  `visible` bit(1) DEFAULT b'1' COMMENT '0=隐藏;1=显示',
  `meta` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Vben 路由元信息(JSON)',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '描述',
  `deleted` bit(1) DEFAULT b'0',
  `create_by` bigint DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT NULL COMMENT '更新人id',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人名称',
  `last_modify_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `INX_SHARED` (`shared`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2002898793212821507 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜单';

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, 'pc-web', '平台管理', NULL, 0, '/plat', 'BasicLayout', 0, 'clarity-thin-client-line', 'directory', b'1', b'1', b'0', b'0', b'1', NULL, '123123', b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2025-12-10 19:26:44');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3, 'pc-web', '系统管理', NULL, 0, '/sys', 'BasicLayout', 3, 'ant-design:dashboard-filled', 'directory', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (50, 'pc-web', '开发平台', NULL, 0, '/dev', 'BasicLayout', 50, 'ant-design:appstore-add-outlined', 'directory', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (104, 'pc-web', '租户管理', NULL, 1, '/plat/tenant', NULL, 104, 'ant-design:code-sandbox-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (301, 'pc-web', '组织架构', NULL, 3, '/sys/structure', NULL, 301, 'ant-design:user-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (302, 'pc-web', '权限管理', NULL, 3, '/sys/auth', NULL, 302, 'ant-design:security-scan-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (310, 'pc-web', '运维监控', NULL, 3, '/sys/monitor', NULL, 310, 'lucide:monitor-cog', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (350, 'pc-web', '安全中心', NULL, 1, '/plat/security', NULL, 350, 'carbon-application', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (5005, 'pc-web', '网关管理', NULL, 50, '/dev/gateway', NULL, 5005, 'ant-design:gateway-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (10401, 'pc-web', '租户中心', 'tenant:list', 104, '/plat/tenant/list', '/wemirr/platform/tenant/index', 10401, 'ant-design:code-sandbox-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (30101, 'pc-web', '用户管理', 'sys:user:page', 301, '/sys/user', '/wemirr/system/user/index', 30101, 'heroicons-outline:user-group', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (30102, 'pc-web', '机构管理', 'sys:org:page', 301, '/sys/org', '/wemirr/system/org/index', 30102, 'codicon-organization', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (30202, 'pc-web', '角色管理', 'sys:role:page', 302, '/system/role', '/wemirr/system/auth/role/index', 30202, 'eos-icons:role-binding-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (31005, 'pc-web', '登录日志', 'monitor:log:login', 310, '/sys/monitor/login-log', '/wemirr/system/log/login-log', 31005, 'mdi:account-security-outline', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (31006, 'pc-web', '操作日志', 'monitor:log:opt', 310, '/sys/monitor/opt-log', '/wemirr/system/log/opt-log', 31006, 'carbon-operation', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (35005, 'pc-web', '菜单管理', 'sys:menu:page', 350, '/plat/menu', '/wemirr/platform/security/menu/index', 35005, 'ant-design:menu-unfold-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (500501, 'pc-web', '限流规则', 'dev:gateway:limit', 5005, '/dev/gateway/limit', '/wemirr/develop/gateway/limit/index', 500501, 'carbon-rule', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (500502, 'pc-web', '限访名单', 'dev:gateway:blacklist', 5005, '/dev/gateway/blacklist', '/wemirr/develop/gateway/blacklist/index', 500502, 'carbon-ai-status-rejected', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (500503, 'pc-web', '网关路由', 'dev:gateway:route', 5005, '/dev/gateway/route', '/wemirr/develop/gateway/route/index', 500503, 'mdi-router-wireless-settings', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', '2024-12-06 14:52:11', 1, '平台管理员', '2024-12-06 14:52:11');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1040101, 'pc-web', '添加', 'tenant:add', 10401, '', NULL, 1040101, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1040102, 'pc-web', '编辑', 'tenant:edit', 10401, '', NULL, 1040102, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '编辑按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:17', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1040103, 'pc-web', '删除', 'tenant:remove', 10401, '', NULL, 1040103, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '删除按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1040106, 'pc-web', '数据初始化', 'tenant:init-script', 10401, '', NULL, 1040106, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '基础数据初始化', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010101, 'pc-web', '添加', 'sys:user:add', 30101, '', NULL, 3010101, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010102, 'pc-web', '编辑', 'sys:user:edit', 30101, '', NULL, 3010102, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '编辑按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:17', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010103, 'pc-web', '删除', 'sys:user:remove', 30101, '', NULL, 3010103, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '删除按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010104, 'pc-web', '重置密码', 'sys:user:reset', 30101, '', NULL, 3010104, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010201, 'pc-web', '添加', 'sys:org:add', 30102, '', NULL, 3010201, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010202, 'pc-web', '编辑', 'sys:org:edit', 30102, '', NULL, 3010202, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '编辑按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:17', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3010203, 'pc-web', '删除', 'sys:org:remove', 30102, '', NULL, 3010203, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '删除按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020101, 'pc-web', '添加', 'sys:menu:add', 302, '', NULL, 3020101, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020102, 'pc-web', '编辑', 'sys:menu:edit', 302, '', NULL, 3020102, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '编辑按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:17', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020103, 'pc-web', '删除', 'sys:menu:remove', 302, '', NULL, 3020103, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '删除按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020201, 'pc-web', '添加', 'sys:role:add', 30202, '', NULL, 3020201, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '新增按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:00', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020202, 'pc-web', '编辑', 'sys:role:edit', 30202, '', NULL, 3020202, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '编辑按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:17', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020203, 'pc-web', '删除', 'sys:role:remove', 30202, '', NULL, 3020203, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '删除按钮权限', b'0', 1, '平台管理员', '2024-12-07 17:52:36', 1, '平台管理员', '2024-12-07 17:52:00');
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020207, 'pc-web', '分配用户', 'sys:role:assign-users', 30202, '', NULL, 3020207, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '分配用户', b'0', 1, '平台管理员', '2024-12-09 15:54:08', NULL, NULL, NULL);
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (3020208, 'pc-web', '分配资源', 'sys:role:assign-resource', 30202, '', NULL, 3020208, '', 'button', b'1', NULL, b'0', b'0', b'1', NULL, '分配资源', b'0', 1, '平台管理员', '2024-12-09 15:55:02', NULL, NULL, NULL);

CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint DEFAULT NULL COMMENT '租户编码',
  `code` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '角色编码',
  `name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
  `scope_type` tinyint DEFAULT NULL COMMENT '数据权限范围，值越大，权限越大',
  `status` tinyint(1) DEFAULT '0' COMMENT '0=正常1=禁用',
  `super` tinyint(1) DEFAULT '0' COMMENT '0=非 1=管理员',
  `readonly` tinyint(1) DEFAULT '0' COMMENT '是否内置角色',
  `create_by` bigint DEFAULT '0' COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT '0' COMMENT '更新人id',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2011807969322373122 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='角色';

INSERT INTO `sys_role` (`id`, `tenant_id`, `code`, `name`, `description`, `scope_type`, `status`, `super`, `readonly`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, 1, 'PLATFORM-ADMIN', '平台管理员', '平台管理员，拥有所有数据可视权', 50, 1, 1, 1, 0, '超级管理员', '2019-10-25 13:46:00', 57, NULL, '2024-12-25 06:03:52');
INSERT INTO `sys_role` (`id`, `tenant_id`, `code`, `name`, `description`, `scope_type`, `status`, `super`, `readonly`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2, 1, 'TENANT-ADMIN', '租户管理员', '租户管理员', 50, 1, 0, 1, 1, '长风一梦8888', '2022-06-07 07:52:29', 0, NULL, '2024-07-19 00:58:14');
INSERT INTO `sys_role` (`id`, `tenant_id`, `code`, `name`, `description`, `scope_type`, `status`, `super`, `readonly`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (8, 8, 'TENANT-ADMIN', '租户管理员', '租户管理员', 50, 1, 0, 1, 1, '长风一梦8888', '2022-06-07 07:52:29', 0, NULL, '2024-07-19 00:58:14');

CREATE TABLE `sys_role_res` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `res_id` bigint NOT NULL COMMENT '资源ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT NULL COMMENT '最近修改人',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '最近修改人',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2011808297283391493 DEFAULT CHARSET=utf8mb3 COMMENT='角色权限表';

INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930370, 1, 1, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930371, 1, 3, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930375, 1, 50, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930377, 1, 104, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930388, 1, 301, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930389, 1, 302, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930392, 1, 310, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930394, 1, 350, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930400, 1, 5005, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930407, 1, 10401, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930433, 1, 30101, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930434, 1, 30102, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930436, 1, 30202, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930445, 1, 31005, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930446, 1, 31006, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930450, 1, 35005, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930477, 1, 500501, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930478, 1, 500502, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930479, 1, 500503, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930494, 1, 1040101, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930495, 1, 1040102, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930496, 1, 1040103, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930498, 1, 1040106, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930508, 1, 3010101, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930509, 1, 3010102, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930510, 1, 3010103, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930511, 1, 3010104, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930513, 1, 3010201, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930514, 1, 3010202, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930515, 1, 3010203, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930519, 1, 3020101, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930520, 1, 3020102, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930521, 1, 3020103, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930522, 1, 3020201, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930523, 1, 3020202, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930524, 1, 3020203, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930525, 1, 3020207, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (2002898793279930526, 1, 3020208, 0, 1, '系统管理员', '2025-12-27 04:24:52', NULL, NULL, NULL);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200000, 2, 3, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200001, 2, 301, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200002, 2, 302, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200005, 2, 310, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200006, 2, 30101, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200007, 2, 30102, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200009, 2, 30202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200016, 2, 31005, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200017, 2, 31006, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200018, 2, 3010101, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200019, 2, 3010102, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200020, 2, 3010103, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200021, 2, 3010104, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200023, 2, 3010201, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200024, 2, 3010202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200025, 2, 3010203, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200029, 2, 3020201, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200030, 2, 3020202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200031, 2, 3020203, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200032, 2, 3020207, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (200033, 2, 3020208, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800000, 8, 3, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800001, 8, 301, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800002, 8, 302, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800005, 8, 310, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800006, 8, 30101, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800007, 8, 30102, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800009, 8, 30202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800016, 8, 31005, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800017, 8, 31006, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800018, 8, 3010101, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800019, 8, 3010102, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800020, 8, 3010103, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800021, 8, 3010104, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800023, 8, 3010201, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800024, 8, 3010202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800025, 8, 3010203, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800029, 8, 3020201, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800030, 8, 3020202, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800031, 8, 3020203, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800032, 8, 3020207, FALSE);
INSERT INTO `sys_role_res` (`id`, `role_id`, `res_id`, `deleted`) VALUES (800033, 8, 3020208, FALSE);

CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT NULL COMMENT '最近修改人',
  `last_modify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '最近修改人',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2011808140726800387 DEFAULT CHARSET=utf8mb3 COMMENT='角色权限表';

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, 1, 1, 0, 1, '系统管理员', '2026-01-14 13:11:19', NULL, NULL, NULL);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (8, 8, 8, 0, 1, '系统管理员', '2026-01-14 13:11:22', NULL, NULL, NULL);

CREATE TABLE `t_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户名称',
  `type` tinyint DEFAULT '0' COMMENT '0=其它,1=企业',
  `alias` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '简称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'LOGO',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户邮箱',
  `status` bit(1) DEFAULT b'0' COMMENT '是否启用 0=未锁定 1=锁定(逻辑删除用)',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系人方式',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  `deleted` bit(1) DEFAULT b'0',
  `create_by` bigint DEFAULT '0' COMMENT '创建人id',
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT '0' COMMENT '更新人id',
  `last_modify_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人名称',
  `last_modify_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1864956411491020802 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户信息';

INSERT INTO `t_tenant` (`id`, `code`, `name`, `type`, `alias`, `logo`, `email`, `status`, `contact_person`, `contact_phone`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, '0000', '平台团队', 1, NULL, NULL, NULL, b'1', '', '', NULL, b'0', 0, NULL, '2021-07-31 07:27:08', 1, '长风一梦', '2024-12-10 08:23:47');
INSERT INTO `t_tenant` (`id`, `code`, `name`, `type`, `alias`, `logo`, `email`, `status`, `contact_person`, `contact_phone`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (8, '8888', '内部团队', 1, NULL, NULL, NULL, b'1', '', '', NULL, b'0', 0, NULL, '2021-07-31 07:27:08', 1, '平台管理员', '2026-01-14 05:07:58');

CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '昵称',
  `org_id` bigint DEFAULT NULL COMMENT '组织ID',
  `readonly` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否内置',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '手机',
  `sex` tinyint DEFAULT '1' COMMENT '性别',
  `type` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0=平台管理员；1=租户管理员',
  `status` bit(1) DEFAULT b'0' COMMENT '状态 \n1启用 0禁用',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '头像',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `last_login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '最后一次登录IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `deleted` bit(1) DEFAULT b'0',
  `create_by` bigint DEFAULT '0' COMMENT '创建人id',
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modify_by` bigint DEFAULT '0' COMMENT '更新人id',
  `last_modify_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人名称',
  `last_modify_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UN_ACCOUNT_TENANT` (`username`,`tenant_id`) USING BTREE COMMENT '账号唯一约束'
) ENGINE=InnoDB AUTO_INCREMENT=2011807735754166274 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户';

INSERT INTO `t_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `org_id`, `readonly`, `email`, `mobile`, `sex`, `type`, `status`, `avatar`, `description`, `birthday`, `last_login_ip`, `last_login_time`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (1, 1, 'admin', '{bcrypt}$2a$10$R2AdNVf402GnqcJejdjY..wOHP5hFt5x0vz5qXdTVG.udcdFmqu.K', '平台管理员', 100, b'0', NULL, NULL, 1, '0', b'1', NULL, NULL, NULL, NULL, NULL, b'0', 0, '系统管理员', '2020-10-16 03:25:36', 1, '平台管理员', '2026-01-15 14:26:06');
INSERT INTO `t_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `org_id`, `readonly`, `email`, `mobile`, `sex`, `type`, `status`, `avatar`, `description`, `birthday`, `last_login_ip`, `last_login_time`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`) VALUES (8, 8, 'admin', '{bcrypt}$2a$10$CUJcTWhs.tzppV2OG2dkkeeE3M11tTi.Pb9pV6sOXcFPyqQIj6q9K', '租户8888', 800, b'0', NULL, NULL, 1, '1', b'1', NULL, NULL, NULL, NULL, NULL, b'0', 1, '平台管理员', '2025-12-11 17:38:50', NULL, NULL, NULL);
