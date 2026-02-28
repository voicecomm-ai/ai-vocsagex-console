-- 插入模型菜单
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
VALUES ('模型', 'model', 0, '', '/model', 0, 1, '2025-05-26 10:54:08', '2025-06-03 09:47:24', 1, 1);

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'model'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '模型广场', 'modelSquare', id, 'modelSquare', '/main/model/square', 0, 1, '2025-05-26 10:54:08', '2025-06-03 09:47:36', 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'model'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '模型管理', 'modelManage', id, 'modelManage', '/main/model/manage', 0, 2, '2025-05-26 10:54:08', '2025-06-03 09:47:38', 1, 1
FROM model_menu_id;

-- 模型广场查看权限
WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'modelSquare'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '查看', 'modelSquareView', id, '', '/main/model/square/view', 1, 1, NOW(), NOW(), 1, 1
FROM menu_opteration;

-- 模型管理查看权限
WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'modelManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '查看', 'modelManageView', id, '', '/main/model/manage/view', 1, 1, NOW(), NOW(), 1, 1
FROM menu_opteration;


-- 模型管理操作权限
WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'modelManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '操作', 'modelManageOperation', id, '', '/main/model/manage/operation', 1, 2, NOW(), NOW(), 1, 1
FROM menu_opteration;

-- 刷新库中主键id
SELECT setval(pg_get_serial_sequence('menu', 'id'),(SELECT MAX(id) FROM menu));


-- 为超级管理员角色绑定模型菜单
WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'model'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, '2025-05-20 09:38:27', '2025-05-20 09:38:27', 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelSquare'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, '2025-05-20 09:38:27', '2025-05-20 09:38:27', 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelManage'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, '2025-05-20 09:38:27', '2025-05-20 09:38:27', 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelSquareView'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelManageView'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM model_menu_id;

WITH model_menu_id AS (
    SELECT id FROM menu WHERE sign = 'modelManageOperation'
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM model_menu_id;

SELECT setval(pg_get_serial_sequence('role_menu_relation', 'id'),(SELECT MAX(id) FROM role_menu_relation));
