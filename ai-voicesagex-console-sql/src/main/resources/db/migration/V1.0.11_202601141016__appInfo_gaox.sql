ALTER TABLE "public"."workflows_publish_history"
    ADD COLUMN "app_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  ADD COLUMN "app_icon_url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;
COMMENT ON COLUMN "public"."workflows_publish_history"."app_name" IS '应用名称';
COMMENT ON COLUMN "public"."workflows_publish_history"."app_icon_url" IS '应用图标';