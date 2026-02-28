ALTER TABLE "public"."knowledge_base_document"
  ADD COLUMN "process_status" varchar(32) NOT NULL DEFAULT 'WAITING';

COMMENT ON COLUMN "public"."knowledge_base_document"."process_status" IS '文档处理状态';
