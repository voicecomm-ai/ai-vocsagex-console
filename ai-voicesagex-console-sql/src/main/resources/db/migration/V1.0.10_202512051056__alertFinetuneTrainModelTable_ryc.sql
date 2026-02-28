--模型微调修改字段
COMMENT ON TABLE "public"."finetune_model" IS '微调模型';


ALTER TABLE "public"."finetune_model" ADD COLUMN "code_url" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

COMMENT ON COLUMN "public"."finetune_model"."code_url" IS '模型代码文件';


ALTER TABLE "public"."finetune_model" ADD COLUMN "is_deploy" bool NOT NULL DEFAULT false;

COMMENT ON COLUMN "public"."finetune_model"."is_deploy" IS '是否部署';



-- 模型训练表修改字段
ALTER TABLE "public"."train_model" RENAME COLUMN "is_deploy_success" TO "is_deploy";

COMMENT ON COLUMN "public"."train_model"."is_deploy" IS '是否部署';


update model_category set "name" = '标签' where is_built = false;

update train_model_category set "name" = '标签' where is_built = false;

update finetune_model_category set "name" = '标签' where is_built = false;