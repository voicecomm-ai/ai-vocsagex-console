ALTER TABLE "public"."model" ADD COLUMN "internal_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "is_support_distributed_train" bool NOT NULL DEFAULT false;

ALTER TABLE "public"."model" ADD COLUMN "train_frame" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'JAX'::character varying;

ALTER TABLE "public"."model" ADD COLUMN "quantified_storage_type" int2 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "quantified_storage_url" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "weight_storage_type" int2 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "weight_storage_url" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "code_url" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "cpu_cores_num" int4 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "memory_size" int4 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "is_selected_gpu" bool NOT NULL DEFAULT false;

ALTER TABLE "public"."model" ADD COLUMN "gpu_num" int4 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "generate_status" int2 NOT NULL DEFAULT 0;

ALTER TABLE "public"."model" ADD COLUMN "task_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "task_info" jsonb NOT NULL DEFAULT '{}'::jsonb;

ALTER TABLE "public"."model" ADD COLUMN "quantified_storage_dir" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "weight_storage_dir" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

ALTER TABLE "public"."model" ADD COLUMN "code_dir" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;


COMMENT ON COLUMN "public"."model"."internal_name" IS '模型内部名称';

COMMENT ON COLUMN "public"."model"."is_support_distributed_train" IS '是否支持分布式训练';

COMMENT ON COLUMN "public"."model"."train_frame" IS '分布式训练框架';

COMMENT ON COLUMN "public"."model"."quantified_storage_type" IS '量化模型存储方式 0：输入路径；1：上传文件';

COMMENT ON COLUMN "public"."model"."quantified_storage_url" IS '量化模型地址';

COMMENT ON COLUMN "public"."model"."weight_storage_type" IS '权重文件存储方式 0：输入路径；1：上传文件';

COMMENT ON COLUMN "public"."model"."weight_storage_url" IS '权重文件地址';

COMMENT ON COLUMN "public"."model"."code_url" IS '模型代码文件';

COMMENT ON COLUMN "public"."model"."cpu_cores_num" IS 'CPU核数';

COMMENT ON COLUMN "public"."model"."memory_size" IS '内存（MB）';

COMMENT ON COLUMN "public"."model"."is_selected_gpu" IS '是否选择GPU';

COMMENT ON COLUMN "public"."model"."gpu_num" IS 'GPU块数';

COMMENT ON COLUMN "public"."model"."generate_status" IS '生成状态 0：生成中；1：生成成功；2：生成失败';

COMMENT ON COLUMN "public"."model"."task_id" IS '任务id';

COMMENT ON COLUMN "public"."model"."task_info" IS '任务信息';

COMMENT ON COLUMN "public"."model"."quantified_storage_dir" IS '解压后量化模型地址';

COMMENT ON COLUMN "public"."model"."weight_storage_dir" IS '解压后权重文件地址';

COMMENT ON COLUMN "public"."model"."code_dir" IS '解压后模型代码文件';


-- 更新模型上架后的状态
update model set generate_status = 1 where "type" = 1;