--修改菜单级别
UPDATE menu
SET superior_id = (SELECT id FROM menu WHERE sign = 'algorithmModel' LIMIT 1),
    sort = 3
WHERE
    sign = 'modelMyModel';

UPDATE menu
SET superior_id = (SELECT id FROM menu WHERE sign = 'preTrainingModel' LIMIT 1),
    sort = 3
WHERE
    sign = 'modelFineTuning';


--新增评测模型菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'algorithmModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '评测',
       'algorithmEvaluation',
       id,
       'algorithmEvaluation',
       '/main/model/algorithmEvaluation',
       0,
       4
FROM menu_manage_id;


--新增预训练模型的按钮
WITH eval_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'algorithmEvaluation')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看', 'algorithmEvaluationView', id, '', '/main/model/algorithmEvaluation/view', 1, 1
FROM eval_model_id;


WITH eval_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'algorithmEvaluation')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作', 'algorithmEvaluationOperation', id, '', '/main/model/algorithmEvaluation/operation', 1, 2
FROM eval_model_id;


-- 新增关联关系
WITH menu_ids AS (SELECT id
                  FROM menu
                  where sign in
                        ('algorithmEvaluation', 'algorithmEvaluationView', 'algorithmEvaluationOperation'))
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1, id
FROM menu_ids;
