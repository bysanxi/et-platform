-- 停用旧版内置菜单及其授权。可重复执行。
START TRANSACTION;

UPDATE sys_role_res grant_row
JOIN sys_resource resource ON resource.id = grant_row.res_id
SET grant_row.deleted = TRUE
WHERE grant_row.deleted = FALSE
  AND ((resource.id = 3010105 AND resource.permission = 'sys:user:export')
    OR (resource.id = 30303 AND resource.permission = 'i18n:page'));

UPDATE sys_resource
SET deleted = TRUE
WHERE deleted = FALSE
  AND ((id = 3010105 AND permission = 'sys:user:export')
    OR (id = 30303 AND permission = 'i18n:page'));

COMMIT;
