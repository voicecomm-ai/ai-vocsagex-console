--新增微调模型菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'modelManage')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '微调',
       'modelFineTuning',
       id,
       'modelFineTuning',
       '/main/model/modelFineTuning',
       0,
       3
FROM menu_manage_id;


--新增预训练模型的按钮
WITH pre_finetune_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'modelFineTuning')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看', 'modelFineTuningView', id, '', '/main/model/modelFineTuning/view', 1, 1
FROM pre_finetune_model_id;


WITH pre_finetune_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'modelFineTuning')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作', 'modelFineTuningOperation', id, '', '/main/model/modelFineTuning/operation', 1, 2
FROM pre_finetune_model_id;


-- 新增关联关系
WITH menu_ids AS (SELECT id
                  FROM menu
                  where sign in
                        ('modelFineTuning', 'modelFineTuningView', 'modelFineTuningOperation'))
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1, id
FROM menu_ids;
