--新增评测模型菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'preTrainingModel')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '评测',
       'preEvaluation',
       id,
       'algorithmEvaluation',
       '/main/model/preEvaluation',
       0,
       4
FROM menu_manage_id;


--新增预训练模型的按钮
WITH eval_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'preEvaluation')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看', 'preEvaluationView', id, '', '/main/model/preEvaluation/view', 1, 1
FROM eval_model_id;


WITH eval_model_id AS (SELECT id
                               FROM menu
                               WHERE sign = 'preEvaluation')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作', 'preEvaluationOperation', id, '', '/main/model/preEvaluation/operation', 1, 2
FROM eval_model_id;


-- 新增关联关系
WITH menu_ids AS (SELECT id
                  FROM menu
                  where sign in
                        ('preEvaluation', 'preEvaluationView', 'preEvaluationOperation'))
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1, id
FROM menu_ids;
