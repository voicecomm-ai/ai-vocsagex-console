update menu
set "menu_name" = '应用管理',
    "icon"      = 'applicationManage',
    "uri"       = '/main/application/manage',
    "sign"= 'applicationManage',
    "sort"      = 1
where id = 11;


update menu
set "menu_name" = '模型管理',
    "icon"      = 'modelManage',
    "uri"       = '/main/model/manage',
    "sign"= 'modelManage',
    "sort"      = 2
where id = 18;


update menu
set "superior_id" = 0,
    "sort"        = 4
where "menu_name" = '模型广场';


update menu
set "superior_id" = 0,
    "sort"        = 5
where "menu_name" = 'MCP广场';


update menu
set "superior_id" = 0,
    "sort"        = 6
where "menu_name" = '系统管理';

-- 插入资源库菜单
INSERT INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type", "sort")
VALUES ('资源库', 'resource', 0, 'resource', '/main/resource', 0, 3);


-- 为超级管理员角色资源库菜单
WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'resource')
INSERT
INTO role_menu_relation (role_id, menu_id)
SELECT 1,
       id
FROM model_menu_id;


-- 删除原先的应用管理
delete
from menu
where id = 12;

-- 原先的应用管理下的查看操作移动
update menu
set "superior_id" = 11
where id in (13, 14);


-- 更新原先的模型菜单信息
update menu
set "menu_name" = '添加模型',
    "sort"      = 1
where id = 20;


update menu
set "menu_name" = '训练模型',
    "sort"      = 2
where id = 31;


-- 原先的模型管理下的查看操作移动
update menu
set "superior_id" = 18
where id in (22, 23);



update menu
set "menu_name" = '数据集'
where id = 34;



WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'resource')
update menu
set "superior_id" = (SELECT * FROM model_menu_id),
    "sort"        = 1
where id = 34;


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'resource')
update menu
set "superior_id" = (SELECT * FROM model_menu_id),
    "sort"        = 2
where menu_name = '知识库';



WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'resource')
update menu
set "superior_id" = (SELECT * FROM model_menu_id),
    "sort"        = 3
where menu_name = 'MCP管理';

update menu
set "menu_name" = 'MCP'
where menu_name = 'MCP管理';