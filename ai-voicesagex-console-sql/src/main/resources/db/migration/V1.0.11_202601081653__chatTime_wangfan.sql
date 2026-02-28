ALTER TABLE "public"."agent_chat_history"
    ADD COLUMN "last_chat_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

COMMENT ON COLUMN "public"."agent_chat_history"."last_chat_time" IS '上一次聊天时间';