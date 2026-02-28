SELECT setval(pg_get_serial_sequence('menu', 'id'),(SELECT MAX(id) FROM menu));
--新增应用管理下的内置菜单
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'applicationManage')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '基础协作体',
       'applicationIntegrated',
       id,
       'applicationIntegrated',
       '/main/application/manage/integrated',
       0,
       3
FROM menu_manage_id;


/**
  查看
 */
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'applicationIntegrated')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '查看',
       'applicationIntegratedView',
       id,
       '',
       '/main/application/manage/integrated/view',
       1,
       1
FROM menu_manage_id;


/**
  操作
 */
WITH menu_manage_id AS (SELECT id
                        FROM menu
                        WHERE sign = 'applicationIntegrated')
INSERT
INTO menu (menu_name, sign, superior_id, icon, uri, type, sort)
SELECT '操作',
       'applicationIntegratedOperation',
       id,
       '',
       '/main/application/manage/integrated/operation',
       1,
       2
FROM menu_manage_id;


-- 新增关联关系
WITH menu_ids AS (SELECT id
                  FROM menu
                  where sign in
                        ('applicationIntegrated', 'applicationIntegratedView',
                         'applicationIntegratedOperation'))
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1, id
FROM menu_ids;
