-- 插入我的模型菜单

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'model')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT '我的模型',
       'modelMyModel',
       id,
       'myModel',
       '/main/model/myModel',
       0,
       3,
       now(),
       now(),
       1,
       1
FROM model_menu_id;


-- 我的模型查看权限
WITH menu_view AS (SELECT id
                   FROM menu
                   WHERE sign = 'modelMyModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT '查看',
       'modelMyModelView',
       id,
       '',
       '/main/model/myModel/view',
       1,
       1,
       NOW(),
       NOW(),
       1,
       1
FROM menu_view;


-- 我的模型操作权限
WITH menu_opteration AS (SELECT id
                         FROM menu
                         WHERE sign = 'modelMyModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT '操作',
       'modelMyModelOperation',
       id,
       '',
       '/main/model/myModel/operation',
       1,
       2,
       NOW(),
       NOW(),
       1,
       1
FROM menu_opteration;

-- 刷新库中主键id
SELECT setval(pg_get_serial_sequence('menu', 'id'), (SELECT MAX(id) FROM menu));


-- 为超级管理员角色绑定我的模型菜单
WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'modelMyModel')
INSERT
INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT 1,
       id,
       NOW(),
       NOW(),
       1,
       1
FROM model_menu_id;



WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'modelMyModelView')
INSERT
INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT 1,
       id,
       NOW(),
       NOW(),
       1,
       1
FROM model_menu_id;

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'modelMyModelOperation')
INSERT
INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT 1,
       id,
       NOW(),
       NOW(),
       1,
       1
FROM model_menu_id;

SELECT setval(pg_get_serial_sequence('role_menu_relation', 'id'),
              (SELECT MAX(id) FROM role_menu_relation));
