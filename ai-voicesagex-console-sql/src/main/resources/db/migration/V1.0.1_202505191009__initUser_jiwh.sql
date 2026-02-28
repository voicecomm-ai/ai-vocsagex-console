CREATE TABLE sys_user
(
    id SERIAL PRIMARY KEY,
    dept_id integer NOT NULL DEFAULT 0,
    account varchar(255) NOT NULL DEFAULT '',
    username varchar(255) NOT NULL DEFAULT '',
    password varchar(255) NOT NULL DEFAULT '',
    phone varchar(15) NOT NULL DEFAULT '',
    status smallint NOT NULL DEFAULT 0,
    is_account_expired boolean NOT NULL DEFAULT FALSE,
    is_account_locked boolean NOT NULL DEFAULT FALSE,
    is_credentials_expired boolean NOT NULL DEFAULT FALSE,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键';
COMMENT ON COLUMN sys_user.dept_id IS '组织架构id';
COMMENT ON COLUMN sys_user.account IS '账号';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.status IS '状态（0 正常 1 禁用 2 删除）';
COMMENT ON COLUMN sys_user.is_account_expired IS '账号是否过期';
COMMENT ON COLUMN sys_user.is_account_locked IS '账号是否锁定';
COMMENT ON COLUMN sys_user.is_credentials_expired IS '密码是否过期';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '修改时间';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.update_by IS '修改人';

CREATE INDEX idx_account ON sys_user(account);

CREATE TABLE role
(
    id SERIAL PRIMARY KEY,
    dept_id integer NOT NULL DEFAULT 0,
    role_name varchar(255) NOT NULL DEFAULT '',
    description varchar(255) NOT NULL DEFAULT '',
    is_admin smallint NOT NULL DEFAULT 0,
    type smallint NOT NULL DEFAULT 0,
    data_permission integer NOT NULL DEFAULT 1,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE role IS '角色表';
COMMENT ON COLUMN role.id IS '主键';
COMMENT ON COLUMN role.dept_id IS '组织架构id';
COMMENT ON COLUMN role.role_name IS '角色名';
COMMENT ON COLUMN role.description IS '描述';
COMMENT ON COLUMN role.is_admin IS '是否管理员（0 否 1 是）';
COMMENT ON COLUMN role.type IS '类型（0 内置 1 自定义）';
COMMENT ON COLUMN role.data_permission IS '数据权限0超管 1本部门（含下级）2本部门 3仅本人';
COMMENT ON COLUMN role.create_time IS '创建时间';
COMMENT ON COLUMN role.update_time IS '修改时间';
COMMENT ON COLUMN role.create_by IS '创建人';
COMMENT ON COLUMN role.update_by IS '修改人';

CREATE TABLE role_menu_relation
(
    id SERIAL PRIMARY KEY,
    role_id integer NOT NULL DEFAULT 0,
    menu_id integer NOT NULL DEFAULT 0,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE role_menu_relation IS '角色菜单关系表';
COMMENT ON COLUMN role_menu_relation.id IS '主键';
COMMENT ON COLUMN role_menu_relation.role_id IS '角色id';
COMMENT ON COLUMN role_menu_relation.menu_id IS '菜单id';
COMMENT ON COLUMN role_menu_relation.create_time IS '创建时间';
COMMENT ON COLUMN role_menu_relation.update_time IS '修改时间';
COMMENT ON COLUMN role_menu_relation.create_by IS '创建人';
COMMENT ON COLUMN role_menu_relation.update_by IS '修改人';

CREATE TABLE menu
(
    id SERIAL PRIMARY KEY,
    menu_name varchar(255) NOT NULL DEFAULT '',
    sign varchar(255) NOT NULL DEFAULT '',
    superior_id integer NOT NULL DEFAULT 0,
    icon varchar(255) NOT NULL DEFAULT '',
    uri varchar(255) NOT NULL DEFAULT '',
    type smallint NOT NULL DEFAULT 0,
    sort integer NOT NULL DEFAULT 0,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE menu IS '菜单表';
COMMENT ON COLUMN menu.id IS '主键';
COMMENT ON COLUMN menu.menu_name IS '菜单名';
COMMENT ON COLUMN menu.sign IS '标志';
COMMENT ON COLUMN menu.superior_id IS '上级id';
COMMENT ON COLUMN menu.icon IS '图标';
COMMENT ON COLUMN menu.uri IS '路径';
COMMENT ON COLUMN menu.type IS '类别（0 菜单 1 按钮）';
COMMENT ON COLUMN menu.sort IS '排序';
COMMENT ON COLUMN menu.create_time IS '创建时间';
COMMENT ON COLUMN menu.update_time IS '修改时间';
COMMENT ON COLUMN menu.create_by IS '创建人';
COMMENT ON COLUMN menu.update_by IS '修改人';

CREATE TABLE department
(
    id SERIAL PRIMARY KEY,
    department_name varchar(50) NOT NULL DEFAULT '',
    level integer NOT NULL DEFAULT 1,
    remark varchar(200) NOT NULL DEFAULT '',
    parent_id integer NOT NULL DEFAULT 0,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE department IS '组织架构';
COMMENT ON COLUMN department.id IS 'id';
COMMENT ON COLUMN department.department_name IS '部门名称';
COMMENT ON COLUMN department.level IS '部门级别';
COMMENT ON COLUMN department.remark IS '备注';
COMMENT ON COLUMN department.parent_id IS '父级id';
COMMENT ON COLUMN department.create_time IS '创建时间';
COMMENT ON COLUMN department.update_time IS '更新时间';
COMMENT ON COLUMN department.create_by IS '创建人';
COMMENT ON COLUMN department.update_by IS '更新人';

CREATE TABLE user_role_relation
(
    id SERIAL PRIMARY KEY,
    user_id integer NOT NULL DEFAULT 0,
    role_id integer NOT NULL DEFAULT 0,
    create_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by integer NOT NULL DEFAULT 1,
    update_by integer NOT NULL DEFAULT 1
);

COMMENT ON TABLE user_role_relation IS '用户角色关系表';
COMMENT ON COLUMN user_role_relation.id IS '主键';
COMMENT ON COLUMN user_role_relation.user_id IS '用户id';
COMMENT ON COLUMN user_role_relation.role_id IS '角色id';
COMMENT ON COLUMN user_role_relation.create_time IS '创建时间';
COMMENT ON COLUMN user_role_relation.update_time IS '修改时间';
COMMENT ON COLUMN user_role_relation.create_by IS '创建人';
COMMENT ON COLUMN user_role_relation.update_by IS '修改人';

-- 插入初始数据 admin  Pwd13579
INSERT INTO sys_user (dept_id, account, username, password, status, is_account_expired, is_account_locked, is_credentials_expired)
VALUES (1, 'admin', 'admin', '$2a$10$7JeB2zCgDC4hVOMVL/CtT.ybpTZCqKQvPoZItmZZvSie1KwskE8eS', 0, false, false, false);

INSERT INTO role (dept_id, role_name, description, is_admin, type, data_permission, create_time, update_time)
VALUES (1, '超级管理员', '超级管理员', 1, 0, 1, '2025-04-11 22:07:42', '2025-04-11 22:07:42');

INSERT INTO user_role_relation (user_id, role_id, create_by, update_by)
VALUES (1, 1, 1, 1);

INSERT INTO department (department_name, level, remark, parent_id, create_time, update_time, create_by, update_by)
VALUES ('总管部门', 1, '', 0, '2024-10-21 17:22:33', '2024-10-21 17:22:33', 1, 1);

INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by) 
VALUES 
('系统管理', 'accountManage', 0, 'project', '/management/account', 0, 4, '2024-07-12 13:33:27', '2024-08-21 10:30:01', 1, 1);

-- 获取系统管理的ID
WITH system_manage AS (
    SELECT id FROM menu WHERE sign = 'accountManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)

SELECT 
    '用户管理', 'accountList', id, 'user', '/main/system/user', 0, 1, '2024-07-12 13:33:42', '2024-07-15 14:42:05', 1, 1
FROM system_manage;

WITH system_manage AS (
    SELECT id FROM menu WHERE sign = 'accountManage'
)

INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '角色权限', 'rolePermission', id, 'role', '/main/system/role', 0, 2, '2024-07-12 13:33:53', '2024-07-15 14:42:38', 1, 1
FROM system_manage;

WITH system_manage AS (
    SELECT id FROM menu WHERE sign = 'accountManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '部门管理', 'department', id, 'department', '/main/system/department', 0, 3, '2024-07-12 13:33:53', '2024-07-15 14:42:38', 1, 1
FROM system_manage;

-- 获取用户管理的ID
WITH user_manage AS (
    SELECT id FROM menu WHERE sign = 'accountList'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '查看', 'accountListView', id, '', '/management/account/view', 1, 1, '2024-11-05 16:46:00', '2024-11-06 09:51:02', 1, 1
FROM user_manage;

WITH user_manage AS (
    SELECT id FROM menu WHERE sign = 'accountList'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '操作', 'accountListOperation', id, '', '/management/account/operation', 1, 2, '2024-11-05 16:46:00', '2024-11-06 09:52:12', 1, 1
FROM user_manage;

-- 获取角色权限的ID
WITH role_permission AS (
    SELECT id FROM menu WHERE sign = 'rolePermission'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '查看', 'rolePermissionView', id, '', '/management/role/view', 1, 1, '2024-11-05 16:46:00', '2024-11-06 09:52:12', 1, 1
FROM role_permission;

WITH role_permission AS (
    SELECT id FROM menu WHERE sign = 'rolePermission'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '操作', 'rolePermissionOperation', id, '', '/management/role/operation', 1, 2, '2024-11-05 16:46:00', '2024-11-06 09:52:12', 1, 1
FROM role_permission;

-- 获取部门管理的ID
WITH department_manage AS (
    SELECT id FROM menu WHERE sign = 'department'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '查看', 'departmentView', id, '', '/management/department/view', 1, 1, '2024-11-05 16:46:00', '2024-11-06 09:52:12', 1, 1
FROM department_manage;

WITH department_manage AS (
    SELECT id FROM menu WHERE sign = 'department'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '操作', 'departmentOperation', id, '', '/management/department/operation', 1, 2, '2024-11-05 16:46:00', '2024-11-06 09:52:12', 1, 1
FROM department_manage;

-- 插入应用菜单
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
VALUES ('应用', 'application', 0, '', '/application', 0, 2, '2025-05-26 10:54:08', '2025-05-26 10:54:29', 1, 1);

-- 获取应用的ID
WITH application AS (
    SELECT id FROM menu WHERE sign = 'application'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '应用管理', 'applicationManage', id, 'applicationManage', '/main/application/manage', 0, 1, '2025-05-21 09:59:53', '2025-05-26 10:57:02', 1, 1
FROM application;

-- 获取应用管理的ID
WITH application_manage AS (
    SELECT id FROM menu WHERE sign = 'applicationManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '查看', 'applicationManageView', id, '', '/applicationManage/view', 1, 1, '2025-05-21 10:01:13', '2025-05-26 10:57:11', 1, 1
FROM application_manage;

WITH application_manage AS (
    SELECT id FROM menu WHERE sign = 'applicationManage'
)
INSERT INTO menu (menu_name, sign, superior_id, icon, uri, type, sort, create_time, update_time, create_by, update_by)
SELECT 
    '操作', 'applicationManageOperation', id, '', '/applicationManage/operation', 1, 2, '2025-05-21 10:01:19', '2025-05-26 10:58:20', 1, 1
FROM application_manage;

-- 为超级管理员角色绑定所有菜单
WITH menu_ids AS (
    SELECT id FROM menu
)
INSERT INTO role_menu_relation (role_id, menu_id, create_time, update_time, create_by, update_by)
SELECT 
    1, id, '2025-05-20 09:38:27', '2025-05-20 09:38:27', 1, 1
FROM menu_ids;
