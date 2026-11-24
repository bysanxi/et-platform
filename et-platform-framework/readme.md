## et-framework-core

核心基础包,不依赖 `WEB` 容器,可以独立的SDK

## et-platform-spring-boot-starter

核心容器包,包括权限,数据库,和容器等核心组件

## et-feign-spring-boot-starter

feign 插件包,增强对 `feign` 使用,可以自动生成 `Token`注入、代理IP服务（即便服务未注册,可以直接连接访问）,上游请求头复制

## et-security-spring-boot-starter

基于 Sa-Token 提供用户身份、租户上下文和角色权限。

## et-redis-spring-boot-starter

基于 Redis 实现的分布式组件

## et-mybatis-plus-spring-boot-starter

集成 MyBatis-Plus，提供基于 `tenant_id` 的数据隔离、数据权限、分页和实体字段填充。
