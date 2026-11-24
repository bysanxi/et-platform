-- 停用已移除的 IAM 内置菜单和授权，保留历史数据及自定义子菜单的目录。
START TRANSACTION;

UPDATE sys_resource r
LEFT JOIN sys_resource child ON child.parent_id = r.id AND child.deleted = FALSE
SET r.deleted = TRUE
WHERE r.client_id = 'pc-web' AND r.deleted = FALSE AND child.id IS NULL
  AND ((r.id = 1040107 AND r.permission = 'tenant:refresh-dict')
    OR (r.id = 1070601 AND r.permission = 'dict:add')
    OR (r.id = 1070602 AND r.permission = 'dict:edit')
    OR (r.id = 1070603 AND r.permission = 'dict:remove')
    OR (r.id = 1070605 AND r.permission = 'dict:refresh')
    OR (r.id = 3010301 AND r.permission = 'sys:position:add')
    OR (r.id = 3010302 AND r.permission = 'sys:position:edit')
    OR (r.id = 3010303 AND r.permission = 'sys:position:remove')
    OR (r.id = 3030101 AND r.permission = 'tenant:dict:add')
    OR (r.id = 3030102 AND r.permission = 'tenant:dict:edit')
    OR (r.id = 3030103 AND r.permission = 'tenant:dict:remove')
    OR (r.id = 3060501 AND r.permission = 'message:template:add')
    OR (r.id = 3060502 AND r.permission = 'message:template:edit')
    OR (r.id = 3060503 AND r.permission = 'message:template:remove')
    OR (r.id = 3060504 AND r.permission = 'message:template:publish'));

UPDATE sys_resource r
LEFT JOIN sys_resource child ON child.parent_id = r.id AND child.deleted = FALSE
SET r.deleted = TRUE
WHERE r.client_id = 'pc-web' AND r.deleted = FALSE AND child.id IS NULL
  AND ((r.id = 10706 AND r.permission = 'dict:list')
    OR (r.id = 10707 AND r.path = '/plat/setting/area')
    OR (r.id = 30103 AND r.permission = 'sys:position:page')
    OR (r.id = 30301 AND r.permission = 'tenant:dict:list')
    OR (r.id = 30604 AND r.permission = 'message:channel:setting')
    OR (r.id = 30605 AND r.permission = 'message:template:page')
    OR (r.id = 30606 AND r.permission = 'message:list')
    OR (r.id = 30607 AND r.permission = 'message:publish')
    OR (r.id = 30608 AND r.permission = 'message:subscribe-list')
    OR (r.id = 31003 AND r.permission = 'monitor:online:token-list')
    OR (r.id = 35003 AND r.permission = 'plat:app:page'));

UPDATE sys_resource r
LEFT JOIN sys_resource child ON child.parent_id = r.id AND child.deleted = FALSE
SET r.deleted = TRUE
WHERE r.client_id = 'pc-web' AND r.deleted = FALSE AND child.id IS NULL
  AND ((r.id = 107 AND r.path = '/plat/setting')
    OR (r.id = 303 AND r.path = '/sys/setting')
    OR (r.id = 306 AND r.path = '/sys/message'));

UPDATE sys_role_res g
JOIN sys_resource r ON r.id = g.res_id
SET g.deleted = TRUE
WHERE r.client_id = 'pc-web' AND r.deleted = TRUE AND g.deleted = FALSE
  AND ((r.id = 107 AND r.path = '/plat/setting')
    OR (r.id = 303 AND r.path = '/sys/setting')
    OR (r.id = 306 AND r.path = '/sys/message')
    OR (r.id = 10706 AND r.permission = 'dict:list')
    OR (r.id = 10707 AND r.path = '/plat/setting/area')
    OR (r.id = 30103 AND r.permission = 'sys:position:page')
    OR (r.id = 30301 AND r.permission = 'tenant:dict:list')
    OR (r.id = 30604 AND r.permission = 'message:channel:setting')
    OR (r.id = 30605 AND r.permission = 'message:template:page')
    OR (r.id = 30606 AND r.permission = 'message:list')
    OR (r.id = 30607 AND r.permission = 'message:publish')
    OR (r.id = 30608 AND r.permission = 'message:subscribe-list')
    OR (r.id = 31003 AND r.permission = 'monitor:online:token-list')
    OR (r.id = 35003 AND r.permission = 'plat:app:page')
    OR (r.id = 1040107 AND r.permission = 'tenant:refresh-dict')
    OR (r.id = 1070601 AND r.permission = 'dict:add')
    OR (r.id = 1070602 AND r.permission = 'dict:edit')
    OR (r.id = 1070603 AND r.permission = 'dict:remove')
    OR (r.id = 1070605 AND r.permission = 'dict:refresh')
    OR (r.id = 3010301 AND r.permission = 'sys:position:add')
    OR (r.id = 3010302 AND r.permission = 'sys:position:edit')
    OR (r.id = 3010303 AND r.permission = 'sys:position:remove')
    OR (r.id = 3030101 AND r.permission = 'tenant:dict:add')
    OR (r.id = 3030102 AND r.permission = 'tenant:dict:edit')
    OR (r.id = 3030103 AND r.permission = 'tenant:dict:remove')
    OR (r.id = 3060501 AND r.permission = 'message:template:add')
    OR (r.id = 3060502 AND r.permission = 'message:template:edit')
    OR (r.id = 3060503 AND r.permission = 'message:template:remove')
    OR (r.id = 3060504 AND r.permission = 'message:template:publish'));

COMMIT;
