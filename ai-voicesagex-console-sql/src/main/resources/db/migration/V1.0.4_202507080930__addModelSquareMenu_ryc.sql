-- 插入模型广场操作菜单
WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'modelSquare'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '操作', 'modelSquareOperation', id, '', '/main/model/square/operation', 1, 2, NOW(), NOW(), 1, 1
FROM menu_opteration;

-- 刷新库中主键id
SELECT setval(pg_get_serial_sequence('menu', 'id'),(SELECT MAX(id) FROM menu));


-- 为超级管理员角色绑定模型菜单
WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelSquareOperation'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM model_menu_id;

SELECT setval(pg_get_serial_sequence('role_menu_relation', 'id'),(SELECT MAX(id) FROM role_menu_relation));
