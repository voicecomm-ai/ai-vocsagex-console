ALTER TABLE "public"."workflow_node_executions"
    ADD COLUMN "tool_app_id" int4 NOT NULL DEFAULT 0;

COMMENT ON COLUMN "public"."workflow_node_executions"."tool_app_id" IS '嵌套的agent或者workflow的appId';