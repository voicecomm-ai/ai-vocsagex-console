ALTER TABLE "public"."mcp"
    ADD COLUMN "transport" varchar(100) NOT NULL DEFAULT 'streamable_http';

COMMENT ON COLUMN "public"."mcp"."transport" IS '调用方式  streamable_http，stdio';