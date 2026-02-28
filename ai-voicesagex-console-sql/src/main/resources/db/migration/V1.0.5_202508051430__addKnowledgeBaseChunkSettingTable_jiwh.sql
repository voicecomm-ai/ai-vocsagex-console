ALTER TABLE knowledge_base ADD COLUMN chunk_identifier varchar(32);
ALTER TABLE knowledge_base ADD COLUMN chunk_max_length integer;
ALTER TABLE knowledge_base ADD COLUMN chunk_overlap_length integer;
ALTER TABLE knowledge_base ADD COLUMN enable_text_process_first_rule boolean;
ALTER TABLE knowledge_base ADD COLUMN enable_text_process_second_rule  boolean;
ALTER TABLE knowledge_base ADD COLUMN parent_chunk_context varchar(32);
ALTER TABLE knowledge_base ADD COLUMN paragraph_chunk_identifier varchar(32);
ALTER TABLE knowledge_base ADD COLUMN paragraph_chunk_max_length integer;

COMMENT ON COLUMN knowledge_base.parent_chunk_context IS '父块作用上下文：FULLTEXT;PARAGRAPH';
COMMENT ON COLUMN knowledge_base.enable_text_process_first_rule IS '替换掉连续的空格、换行符和制表符';
COMMENT ON COLUMN knowledge_base.enable_text_process_second_rule IS '删除所有 URL 和电子邮件地址';

ALTER TABLE knowledge_base_document_metadata ADD COLUMN "name" varchar(255);
