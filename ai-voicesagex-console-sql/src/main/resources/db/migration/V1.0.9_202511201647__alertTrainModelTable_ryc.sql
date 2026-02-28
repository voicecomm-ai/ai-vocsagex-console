-- 模型表添加字段
ALTER TABLE "public"."model"
    ALTER COLUMN "train_frame" SET DEFAULT 'PyTorch'::character varying;

ALTER TABLE "public"."model"
    ADD COLUMN "is_special" bool NOT NULL DEFAULT false;

COMMENT
ON COLUMN "public"."model"."is_special" IS '是否特殊';



--训练模型表添加字段
ALTER TABLE "public"."train_model"
    ADD COLUMN "internal_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

COMMENT
ON COLUMN "public"."train_model"."internal_name" IS '模型内部名称';

ALTER TABLE "public"."train_model"
    ADD COLUMN "is_support_distributed_train" bool NOT NULL DEFAULT false;

COMMENT
ON COLUMN "public"."train_model"."is_support_distributed_train" IS '是否支持分布式训练';

ALTER TABLE "public"."train_model"
    ADD COLUMN "train_frame" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'PyTorch'::character varying;

COMMENT
ON COLUMN "public"."train_model"."train_frame" IS '分布式训练框架';

ALTER TABLE "public"."train_model"
    ADD COLUMN "config_text" text COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::text;

COMMENT
ON COLUMN "public"."train_model"."config_text" IS '配置文件脚本';

ALTER TABLE "public"."train_model"
    ADD COLUMN "dataset_id" int4 NOT NULL DEFAULT 0;

COMMENT
ON COLUMN "public"."train_model"."dataset_id" IS '数据集id';

ALTER TABLE "public"."train_model"
    ADD COLUMN "cpu_cores_num" int4 NOT NULL DEFAULT 0;

COMMENT
ON COLUMN "public"."train_model"."cpu_cores_num" IS 'CPU核数';

ALTER TABLE "public"."train_model"
    ADD COLUMN "memory_size" int4 NOT NULL DEFAULT 0;

COMMENT
ON COLUMN "public"."train_model"."memory_size" IS '内存（MB）';

ALTER TABLE "public"."train_model"
    ADD COLUMN "is_selected_gpu" bool NOT NULL DEFAULT false;

COMMENT
ON COLUMN "public"."train_model"."is_selected_gpu" IS '是否选择GPU';

ALTER TABLE "public"."train_model"
    ADD COLUMN "gpu_num" int4 NOT NULL DEFAULT 0;

COMMENT
ON COLUMN "public"."train_model"."gpu_num" IS 'GPU块数';

ALTER TABLE "public"."train_model"
    ADD COLUMN "task_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

COMMENT
ON COLUMN "public"."train_model"."task_id" IS '任务id';

ALTER TABLE "public"."train_model"
    ADD COLUMN "code_dir" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

COMMENT
ON COLUMN "public"."train_model"."code_dir" IS '解压后模型代码文件';

ALTER TABLE "public"."train_model" DROP COLUMN "train_path";

ALTER TABLE "public"."train_model"
    ADD COLUMN "is_deploy_success" bool NOT NULL DEFAULT false;

COMMENT ON COLUMN "public"."train_model"."is_deploy_success" IS '是否部署成功';