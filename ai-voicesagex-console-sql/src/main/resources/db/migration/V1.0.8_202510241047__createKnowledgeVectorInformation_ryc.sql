-- 创建图知识库向量信息
CREATE TABLE "public"."knowledge_graph_vector_information"
(
    "vector_job_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "space_id"      int4                                        NOT NULL DEFAULT 0,
    "create_by"     int4                                        NOT NULL DEFAULT 1,
    "update_by"     int4                                        NOT NULL DEFAULT 1,
    "create_time"   timestamp(0)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"   timestamp(0)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "knowledge_graph_vector_information_pkey" PRIMARY KEY ("vector_job_id")
)
;

ALTER TABLE "public"."knowledge_graph_vector_information"
    OWNER TO "postgres";

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."vector_job_id" IS '向量任务id';

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."space_id" IS '知识库id';

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."create_by" IS '创建人';

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."update_by" IS '更新人';

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."create_time" IS '创建时间';

COMMENT
ON COLUMN "public"."knowledge_graph_vector_information"."update_time" IS '更新时间';

COMMENT
ON TABLE "public"."knowledge_graph_vector_information" IS '图知识库向量信息';