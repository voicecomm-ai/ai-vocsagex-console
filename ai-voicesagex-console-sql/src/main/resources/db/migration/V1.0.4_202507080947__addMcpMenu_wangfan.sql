WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'application')

INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select 'MCP广场',
       'mcpSquare',
       id,
       'mcpSquare',
       '/main/application/mcp/square',
       0,
       2,
       '2025-07-08 09:41:14.093996',
       '2025-07-08 09:41:14.093996',
       1,
       1
from model_menu_id;


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpSquare')
INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select '查看',
       'mcpSquareView',
       id,
       '',
       '/main/application/mcp/square/view',
       1,
       1,
       '2025-07-08 09:41:24.360859',
       '2025-07-08 09:41:24.360859',
       1,
       1
from model_menu_id;


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpSquare')
INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select '操作',
       'mcpSquareOperation',
       id,
       '',
       '/main/application/mcp/square/operation',
       1,
       2,
       '2025-07-08 09:41:28.793545',
       '2025-07-08 09:41:28.793545',
       1,
       1
from model_menu_id;

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'application')

INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select 'MCP管理',
       'mcpManage',
       id,
       'mcpManage',
       '/main/application/mcp/manage',
       0,
       3,
       '2025-07-08 09:41:38.35372',
       '2025-07-08 09:41:38.35372',
       1,
       1
from model_menu_id;

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpManage')
INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select '查看',
       'mcpManageView',
       id,
       '',
       '/main/application/mcp/manage/view',
       1,
       1,
       '2025-07-08 09:42:08.106236',
       '2025-07-08 09:42:08.106236',
       1,
       1
from model_menu_id;


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpManage')
INSERT
INTO "public"."menu" ("menu_name", "sign", "superior_id", "icon", "uri", "type",
                      "sort", "create_time", "update_time", "create_by", "update_by")
select '操作',
       'mcpManageOperation',
       id,
       '',
       '/main/application/mcp/manage/operation',
       1,
       2,
       '2025-07-08 09:42:13.710471',
       '2025-07-08 09:42:13.710471',
       1,
       1
from model_menu_id;

SELECT setval(pg_get_serial_sequence('menu', 'id'), (SELECT MAX(id) FROM menu));


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpSquare')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time",
                                    "create_by", "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;
WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpSquareView')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time",
                                    "create_by", "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;
WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpSquareOperation')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time",
                                    "create_by", "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpManage')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time", "create_by",
                                    "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;

WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpManageView')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time",
                                    "create_by", "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;


WITH model_menu_id AS (SELECT id
                       FROM menu
                       WHERE sign = 'mcpManageOperation')
INSERT
INTO "public"."role_menu_relation" ("role_id", "menu_id", "create_time", "update_time",
                                    "create_by", "update_by")
select 1, id, '2025-07-08 09:54:22.73233', '2025-07-08 09:54:22.73233', 1, 1
from model_menu_id;


SELECT setval(pg_get_serial_sequence('role_menu_relation', 'id'),
              (SELECT MAX(id) FROM role_menu_relation));




