ALTER TABLE "public"."workflow_node_executions"
    ADD COLUMN "loop_index" int4 NOT NULL DEFAULT 0;
COMMENT ON COLUMN "public"."workflow_node_executions"."loop_index" IS '循环index';