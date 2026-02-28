/**
  智能体信息表--子智能体id列表字段，子智能体合作模式字段
 */
ALTER TABLE "public"."agent_info"
    ADD COLUMN "sub_agent_app_ids" int4[] NOT NULL DEFAULT '{}'::integer[];
COMMENT ON COLUMN "public"."agent_info"."sub_agent_app_ids" IS '子智能体id列表';

ALTER TABLE "public"."agent_info"
    ADD COLUMN "cooperate_mode" varchar(30) NOT NULL DEFAULT '';
COMMENT ON COLUMN "public"."agent_info"."cooperate_mode" IS '合作模式   主管Manager，协作Collaboration';


/**
  应用是否内置
 */
ALTER TABLE "public"."application"
    ADD COLUMN "is_integrated" bool NOT NULL DEFAULT false;
COMMENT ON COLUMN "public"."application"."is_integrated" IS '是否内置';


/**
  相关应用表--添加智能体类型字段
 */
ALTER TABLE "public"."application"
    ADD COLUMN "agent_type" varchar(20) NOT NULL DEFAULT '';
COMMENT ON COLUMN "public"."application"."agent_type" IS 'agent类型  single单个，multiple多个';
update "public"."application"
set "agent_type" = 'single'
where "type" = 'agent'
  and "agent_type" = '';


ALTER TABLE "public"."application_experience"
    ADD COLUMN "agent_type" varchar(20) NOT NULL DEFAULT '';
COMMENT ON COLUMN "public"."application_experience"."agent_type" IS 'agent类型  single单个，multiple多个';
update "public"."application_experience"
set "agent_type" = 'single'
where "type" = 'agent'
  and "agent_type" = '';












