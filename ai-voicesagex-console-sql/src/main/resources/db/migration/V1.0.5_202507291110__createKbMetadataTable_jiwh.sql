CREATE TABLE knowledge_base_metadata (
  id SERIAL PRIMARY KEY,
  name varchar(64) NOT NULL,
  type varchar(32) NOT NULL,
  is_built_in boolean NOT NULL DEFAULT false,
  "knowledge_base_id" int4 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN knowledge_base_metadata.id IS '主键';
COMMENT ON COLUMN knowledge_base_metadata.name IS '知识库元数据名称';
COMMENT ON COLUMN knowledge_base_metadata.type IS '元数据类型：String;Number;Time';
COMMENT ON COLUMN knowledge_base_metadata.is_built_in IS '是否内置';
COMMENT ON COLUMN knowledge_base_metadata.create_time IS '创建时间';
COMMENT ON COLUMN knowledge_base_metadata.update_time IS '更新时间';

CREATE TABLE knowledge_base_document_metadata (
  id SERIAL PRIMARY KEY,
  metadata_id int4 NOT NULL DEFAULT 0,
  document_id int4 NOT NULL DEFAULT 0,
  "value" varchar(128),
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

