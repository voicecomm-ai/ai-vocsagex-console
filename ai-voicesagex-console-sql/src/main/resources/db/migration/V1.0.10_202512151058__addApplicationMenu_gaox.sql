INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (50, '未发布', 'applicationUnreleased', 11, 'applicationUnreleased',
        '/main/application/manage/unreleased', 0, 1, '2025-12-15 09:57:12.381167',
        '2025-12-15 09:57:12.381167', 1, 1);
INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (51, '已发布', 'applicationReleased', 11, 'applicationReleased',
        '/main/application/manage/released', 0, 1, '2025-12-15 09:57:39.989065',
        '2025-12-15 09:57:39.989065', 1, 1);
INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (52, '查看', 'applicationUnreleasedView', 50, '', '/main/application/manage/unreleased/view',
        1, 1, '2025-12-15 09:57:48.249683', '2025-12-15 09:57:48.249683', 1, 1);
INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (53, '操作', 'applicationUnreleasedOperation', 50, '',
        '/main/application/manage/unreleased/operation', 1, 2, '2025-12-15 09:58:33.631677',
        '2025-12-15 09:58:33.631677', 1, 1);
INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (54, '查看', 'applicationReleasedView', 51, '', '/main/application/manage/released/view', 1,
        1, '2025-12-15 09:59:17.607994', '2025-12-15 09:59:17.607994', 1, 1);
INSERT INTO "public"."menu" ("id", "menu_name", "sign", "superior_id", "icon", "uri", "type",
                             "sort", "create_time", "update_time", "create_by", "update_by")
VALUES (55, '操作', 'applicationReleasedOperation', 51, '',
        '/main/application/manage/released/operation', 1, 2, '2025-12-15 10:00:04.53226',
        '2025-12-15 10:00:04.53226', 1, 1);
-- 添加菜单角色关联
INSERT INTO role_menu_relation (role_id, menu_id)
SELECT DISTINCT r.role_id,
                m.menu_id
FROM role_menu_relation r
         CROSS JOIN UNNEST(ARRAY[50, 51, 52, 54]) AS m(menu_id)
WHERE r.menu_id = 13;
INSERT INTO role_menu_relation (role_id, menu_id)
SELECT DISTINCT r.role_id,
                m.menu_id
FROM role_menu_relation r
         CROSS JOIN UNNEST(ARRAY[53, 55]) AS m(menu_id)
WHERE r.menu_id = 14;
DELETE
FROM "public"."menu"
WHERE "id" = 13;
DELETE
FROM "public"."menu"
WHERE "id" = 14;
delete
from "public"."role_menu_relation"
where menu_id = 13;
delete
from "public"."role_menu_relation"
where menu_id = 14;

SELECT setval(pg_get_serial_sequence('menu', 'id'), (SELECT MAX(id) FROM menu));