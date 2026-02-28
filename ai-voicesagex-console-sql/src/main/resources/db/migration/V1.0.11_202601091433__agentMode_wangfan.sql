/**
  修改默认值
 */
ALTER TABLE "public"."agent_info"
    ALTER COLUMN "agent_mode" SET DEFAULT '';



ALTER TABLE "public"."application"
    ALTER COLUMN "api_accessable" SET DEFAULT false;