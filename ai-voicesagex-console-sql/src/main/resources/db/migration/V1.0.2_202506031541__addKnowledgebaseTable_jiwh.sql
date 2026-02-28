DROP TYPE IF EXISTS knowledge_base_type;
DROP TYPE IF EXISTS knowledge_base_search_strategy;
DROP TYPE IF EXISTS knowledge_base_chunking_strategy;
DROP TYPE IF EXISTS knowledge_base_parent_chunk_context;
CREATE TYPE knowledge_base_type AS ENUM ('TRAD', 'GRAPH');
CREATE TYPE knowledge_base_search_strategy AS ENUM ('HYBRID', 'FULL_TEXT', 'VECTOR');
CREATE TYPE knowledge_base_chunking_strategy AS ENUM ('COMMON', 'PARENT_CHILD');
CREATE TYPE knowledge_base_parent_chunk_context AS ENUM ('PARAGRAPH', 'FULL_TEXT');

CREATE TABLE knowledge_base (
  id SERIAL PRIMARY KEY,
  name varchar(64) NOT NULL,
  type varchar(32),
  description varchar(400),
  common_segment_identifier varchar(32),
  common_max_segment_length integer,
  common_segment_overlap_length integer,
  embedding_model_id integer,
  enable_multimodal boolean,
  enable_vector_search_rerank boolean,
  vector_search_rerank_model_id integer,
  vector_search_top_k integer,
  vector_search_score float,
  enable_vector_search_score boolean,
  enable_full_text_search_rerank boolean,
  full_text_search_rerank_model_id integer,
  full_text_search_top_k integer,
  full_text_search_score float,
  enable_full_text_search_score boolean,
  hybrid_search_top_k integer,
  enable_hybrid_search_score boolean,
  hybrid_search_score real,
  hybrid_search_rerank_model_id integer,
  hybrid_search_semantic_matching_weight float,
  hybrid_search_keyword_matching_weight float,
  search_strategy varchar(32),
  enable_qa_chunking boolean,
  qa_model_id integer,
  enable_common_text_preprocessing_rule1 boolean,
  enable_common_text_preprocessing_rule2 boolean,
  chunking_strategy varchar(32),
  parent_chunk_context varchar(32),
  child_chunk_segment_identifier varchar(255),
  child_chunk_max_segment_length integer,
  enable_parent_child_text_preprocessing_rule1 boolean,
  enable_parent_child_text_preprocessing_rule2 boolean,
  parent_chunk_paragraph_segment_identifier varchar(255),
  parent_chunk_paragraph_max_segment_length integer,
  "create_by" int4 NOT NULL DEFAULT 1,
  "update_by" int4 NOT NULL DEFAULT 1,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN knowledge_base.id IS '主键';
COMMENT ON COLUMN knowledge_base.name IS '知识库名称';
COMMENT ON COLUMN knowledge_base.type IS '类型（TRAD-传统，GRAPH-图谱）';
COMMENT ON COLUMN knowledge_base.description IS '知识库描述';
COMMENT ON COLUMN knowledge_base.common_segment_identifier IS '通用分段标识符';
COMMENT ON COLUMN knowledge_base.common_max_segment_length IS '通用分段最大长度';
COMMENT ON COLUMN knowledge_base.common_segment_overlap_length IS '通用分段重叠长度';
COMMENT ON COLUMN knowledge_base.embedding_model_id IS 'Embedding模型';
COMMENT ON COLUMN knowledge_base.enable_multimodal IS '多模态';
COMMENT ON COLUMN knowledge_base.enable_vector_search_rerank IS '向量检索是否启用Rerank模型';
COMMENT ON COLUMN knowledge_base.vector_search_rerank_model_id IS '向量检索Rerank模型ID';
COMMENT ON COLUMN knowledge_base.vector_search_top_k IS '向量检索TOP K';
COMMENT ON COLUMN knowledge_base.vector_search_score IS '向量检索Score阈值';
COMMENT ON COLUMN knowledge_base.enable_vector_search_score IS '向量检索是否启用Score阈值';
COMMENT ON COLUMN knowledge_base.enable_full_text_search_rerank IS '全文检索是否启用Rerank模型';
COMMENT ON COLUMN knowledge_base.full_text_search_rerank_model_id IS '全文检索Rerank模型ID';
COMMENT ON COLUMN knowledge_base.full_text_search_top_k IS '全文检索TOP K';
COMMENT ON COLUMN knowledge_base.full_text_search_score IS '全文检索Score阈值';
COMMENT ON COLUMN knowledge_base.enable_full_text_search_score IS '全文检索是否启用Score阈值';
COMMENT ON COLUMN knowledge_base.hybrid_search_top_k IS '混合检索TOP K';
COMMENT ON COLUMN knowledge_base.enable_hybrid_search_score IS '混合检索是否启用Score阈值';
COMMENT ON COLUMN knowledge_base.hybrid_search_score IS '混合检索Score阈值';
COMMENT ON COLUMN knowledge_base.hybrid_search_rerank_model_id IS '混合检索Rerank模型ID';
COMMENT ON COLUMN knowledge_base.hybrid_search_semantic_matching_weight IS '混合检索语义匹配权重';
COMMENT ON COLUMN knowledge_base.hybrid_search_keyword_matching_weight IS '混合检索关键词匹配权重';
COMMENT ON COLUMN knowledge_base.search_strategy IS '检索策略（HYBRID-混合，FULL_TEXT-全文，VECTOR-向量）';
COMMENT ON COLUMN knowledge_base.enable_qa_chunking IS '是否启用QA分段';
COMMENT ON COLUMN knowledge_base.qa_model_id IS 'QA模型ID';
COMMENT ON COLUMN knowledge_base.enable_common_text_preprocessing_rule1 IS '是否启用通用分段文本预处理规则：替换掉连续的空格、换行符和制表符';
COMMENT ON COLUMN knowledge_base.enable_common_text_preprocessing_rule2 IS '是否启用通用分段文本预处理规则：删除所有URL和电子邮件地址';
COMMENT ON COLUMN knowledge_base.chunking_strategy IS '分段策略（COMMON-通用，PARENT_CHILD-父子）';
COMMENT ON COLUMN knowledge_base.parent_chunk_context IS '父块用作上下文（PARAGRAPH-段落，FULL_TEXT-全文）';
COMMENT ON COLUMN knowledge_base.child_chunk_segment_identifier IS '父子分段子块用于检索标识符';
COMMENT ON COLUMN knowledge_base.child_chunk_max_segment_length IS '父子分段子块最大长度';
COMMENT ON COLUMN knowledge_base.enable_parent_child_text_preprocessing_rule1 IS '是否启用父子分段文本预处理规则：替换掉连续的空格、换行符和制表符';
COMMENT ON COLUMN knowledge_base.enable_parent_child_text_preprocessing_rule2 IS '是否启用父子分段文本预处理规则：删除所有URL和电子邮件地址';
COMMENT ON COLUMN knowledge_base.parent_chunk_paragraph_segment_identifier IS '父块段落分段标识符';
COMMENT ON COLUMN knowledge_base.parent_chunk_paragraph_max_segment_length IS '父块段落分段最大长度';

CREATE TABLE knowledge_base_tag (
    id SERIAL PRIMARY KEY,
    name varchar(50) NOT NULL,
    "create_by" int4 NOT NULL DEFAULT 1,
    "update_by" int4 NOT NULL DEFAULT 1,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE knowledge_base_tag IS '知识库标签表';
COMMENT ON COLUMN knowledge_base_tag.id IS '主键';
COMMENT ON COLUMN knowledge_base_tag.name IS '标签名称';

CREATE TABLE knowledge_base_tag_relation (
    id SERIAL PRIMARY KEY,
    knowledge_base_id bigint NOT NULL,
    tag_id integer NOT NULL,
    CONSTRAINT fk_knowledge_base FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id)
        REFERENCES knowledge_base_tag(id) ON DELETE CASCADE,
    CONSTRAINT uk_knowledge_base_tag UNIQUE (knowledge_base_id, tag_id)
);

COMMENT ON TABLE knowledge_base_tag_relation IS '知识库-标签关系表';
COMMENT ON COLUMN knowledge_base_tag_relation.id IS '主键';
COMMENT ON COLUMN knowledge_base_tag_relation.knowledge_base_id IS '知识库ID';
COMMENT ON COLUMN knowledge_base_tag_relation.tag_id IS '标签ID';

CREATE INDEX idx_knowledge_base_label_relation_kb_id ON knowledge_base_tag_relation(knowledge_base_id);
CREATE INDEX idx_knowledge_base_label_relation_tag_id ON knowledge_base_tag_relation(tag_id);

CREATE TABLE "public"."knowledge_base_document" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1
),
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "chunks" jsonb,
  "knowledge_base_id" int4,
  "chunking_strategy" varchar(255) COLLATE "pg_catalog"."default",
  "unique_name" varchar(255) COLLATE "pg_catalog"."default",
  "preview_chunks" jsonb,
  CONSTRAINT "knowledge_base_document_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."knowledge_base_document"
  OWNER TO "postgres";

COMMENT ON COLUMN "public"."knowledge_base_document"."id" IS '主键';

COMMENT ON COLUMN "public"."knowledge_base_document"."name" IS '文档名称';

COMMENT ON COLUMN "public"."knowledge_base_document"."chunks" IS '文档分块';

COMMENT ON COLUMN "public"."knowledge_base_document"."knowledge_base_id" IS '知识库ID';

COMMENT ON COLUMN "public"."knowledge_base_document"."chunking_strategy" IS '分段策略';

COMMENT ON COLUMN "public"."knowledge_base_document"."unique_name" IS '文档唯一名称';

COMMENT ON COLUMN "public"."knowledge_base_document"."preview_chunks" IS '文档分块预览';



CREATE TABLE "public"."knowledge_base_doc_vector"
(
    "id"                int4                                       NOT NULL GENERATED BY DEFAULT AS IDENTITY (
        INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1
),
    "content_id"        varchar(255) COLLATE "pg_catalog"."default",
    "knowledge_base_id" int4,
    "document_id"       int4,
    "retrieve_content"  text COLLATE "pg_catalog"."default",
    "context_content"   text COLLATE "pg_catalog"."default",
    "metadata"          jsonb,
    "usage"             jsonb,
    "process_status"    varchar(32) COLLATE "pg_catalog"."default",
    "vector"            "public"."vector",
    "content_hash"      varchar(255) COLLATE "pg_catalog"."default",
    "chunking_strategy" varchar(32) COLLATE "pg_catalog"."default",
    "status"            varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'ENABLE':: character varying,
    "create_time"       timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"       timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "knowledge_base_doc_vector_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."knowledge_base_doc_vector"
    OWNER TO "postgres";

CREATE TRIGGER "set_update_time_vector"
    BEFORE UPDATE
    ON "public"."knowledge_base_doc_vector"
    FOR EACH ROW
    EXECUTE PROCEDURE "public"."update_timestamp"();

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."content_id" IS '段落id，uuid4';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."knowledge_base_id" IS '知识库id';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."document_id" IS '文件id';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."retrieve_content" IS '检索内容';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."context_content" IS '上下文内容';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."metadata" IS '段落元数据';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."usage" IS 'tokens用量';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."process_status" IS '标记段落的处理状态';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."vector" IS '向量';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."content_hash" IS '段落Hash值';

COMMENT
ON COLUMN "public"."knowledge_base_doc_vector"."chunking_strategy" IS '分段策略';


