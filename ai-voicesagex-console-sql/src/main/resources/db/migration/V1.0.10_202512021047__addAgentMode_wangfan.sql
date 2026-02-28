ALTER TABLE "public"."agent_info"
    ADD COLUMN "agent_mode" varchar(30) NOT NULL DEFAULT 'function_call';

COMMENT ON COLUMN "public"."agent_info"."agent_mode" IS 'agent推理模式  function_call   react';