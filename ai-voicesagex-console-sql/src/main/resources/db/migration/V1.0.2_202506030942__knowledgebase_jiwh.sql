-- 插入知识库菜单
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
VALUES ('RAG知识库', 'knowledge', 0, '', '/main/knowledge', 0, 3, '2025-05-26 10:54:08', '2025-05-26 10:54:29', 1, 1);

WITH menu_ids AS (
    SELECT id FROM menu WHERE sign = 'knowledge'
)

INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, '2025-05-20 09:38:27', '2025-05-20 09:38:27', 1, 1
FROM menu_ids;

WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'knowledge'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '查看', 'knowledgeView', id, '', '/main/knowledge/view', 1, 1, NOW(), NOW(), 1, 1
FROM menu_opteration;
WITH menu_opteration AS (
    SELECT id FROM menu WHERE sign = 'knowledge'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT
    '操作', 'knowledgeOperation', id, '', '/main/knowledge/operation', 1, 1, NOW(), NOW(), 1, 1
FROM menu_opteration;

WITH menu_ids AS (
    SELECT id FROM menu WHERE sign = 'knowledgeView'
)

INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM menu_ids;

WITH menu_ids AS (
    SELECT id FROM menu WHERE sign = 'knowledgeOperation'
)

INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT
    1, id, NOW(), NOW(), 1, 1
FROM menu_ids;


-- WITH menu_ids AS (
--     SELECT id FROM menu WHERE sign = 'knowledge'
-- )
--
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     '传统RAG', 'tradRag', id, '', '/main/knowledge/trad', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1
-- FROM menu_ids;
--
-- WITH menu_opteration AS (
--     SELECT id FROM menu WHERE sign = 'tradRag'
-- )
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     '查看', 'tradRagView', id, '', '/main/knowledge/trad/view', 1, 1, NOW(), NOW(), 1, 1
-- FROM menu_opteration;
-- WITH menu_opteration AS (
--     SELECT id FROM menu WHERE sign = 'tradRag'
-- )
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     '操作', 'tradRagOperation', id, '', '/main/knowledge/trad/operation', 1, 1, NOW(), NOW(), 1, 1
-- FROM menu_opteration;
--
--
--
-- WITH menu_ids AS (
--     SELECT id FROM menu WHERE sign = 'knowledge'
-- )
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     'Graph RAG', 'graphRag', id, '', '/main/knowledge/graph', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1
-- FROM menu_ids;
-- WITH menu_opteration AS (
--     SELECT id FROM menu WHERE sign = 'graphRag'
-- )
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     '查看', 'graphRagView', id, '', '/main/knowledge/graph/view', 1, 1, NOW(), NOW(), 1, 1
-- FROM menu_opteration;
-- WITH menu_opteration AS (
--     SELECT id FROM menu WHERE sign = 'graphRag'
-- )
-- INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
-- SELECT
--     '操作', 'graphRagOperation', id, '', '/main/knowledge/graph/operation', 1, 1, NOW(), NOW(), 1, 1
-- FROM menu_opteration;
