-- 删除下级菜单的关联关系
WITH add_model_id AS (SELECT id
                      FROM menu
                      WHERE menu_name = '添加模型')
delete
from role_menu_relation
where menu_id in (select id from menu where superior_id = (select id from add_model_id));


-- 删除当前菜单的关联关系
WITH add_model_id AS (SELECT id
                      FROM menu
                      WHERE menu_name = '添加模型')
delete
from role_menu_relation
where menu_id in (select id from add_model_id);


-- 删除当前菜单的下级菜单
WITH add_model_id AS (SELECT id
                      FROM menu
                      WHERE menu_name = '添加模型')
delete
from menu
where superior_id = (select id from add_model_id);


-- 删除当前菜单
delete
from menu
WHERE menu_name = '添加模型';


--新增算法模型菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE menu_name = '模型管理')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '算法模型', 'algorithmModel', id, 'algorithmModel', '/main/model/algorithmModel', 0, 1
FROM menu_manage_id;


--新增算法模型的按钮
WITH algorithm_model_id AS (SELECT id
                            FROM menu
                            WHERE sign = 'algorithmModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看', 'algorithmModelView', id, '', '/main/model/algorithmModel/view', 1, 1
FROM algorithm_model_id;


WITH algorithm_model_id AS (SELECT id
                            FROM menu
                            WHERE sign = 'algorithmModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作', 'algorithmModelOperation', id, '', '/main/model/algorithmModel/operation', 1, 2
FROM algorithm_model_id;


--新增预训练模型菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE menu_name = '模型管理')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '预训练模型',
       'preTrainingModel',
       id,
       'preTrainingModel',
       '/main/model/preTrainingModel',
       0,
       2
FROM menu_manage_id;


--新增预训练模型的按钮
WITH pre_training_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'preTrainingModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看', 'preTrainingModelView', id, '', '/main/model/preTrainingModel/view', 1, 1
FROM pre_training_model_id;


WITH pre_training_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'preTrainingModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作', 'preTrainingModelOperation', id, '', '/main/model/preTrainingModel/operation', 1, 2
FROM pre_training_model_id;


-- 新增关联关系
WITH menu_ids AS (SELECT id
                  FROM menu
                  where sign in
                        ('algorithmModel', 'algorithmModelView', 'algorithmModelOperation',
                         'preTrainingModel', 'preTrainingModelView', 'preTrainingModelOperation'))
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1, id
FROM menu_ids;

-- 删除多余的菜单
WITH delete_menu_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'modelManageView')
delete
from role_menu_relation
where menu_id in (select id from delete_menu_id);

WITH delete_menu_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'modelManageOperation')
delete
from role_menu_relation
where menu_id in (select id from delete_menu_id);


delete FROM menu WHERE sign = 'modelManageView';
delete FROM menu WHERE sign = 'modelManageOperation';