-- 创建检索测试记录表
CREATE TABLE IF NOT EXISTS retrieval_test_record (
    id SERIAL PRIMARY KEY,
    document_id INTEGER NOT NULL,
    query TEXT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_retrieval_test_record_document_id
        FOREIGN KEY (document_id) REFERENCES knowledge_base_document(id) ON DELETE CASCADE
);

-- 添加表注释
COMMENT ON TABLE retrieval_test_record IS '检索测试记录表';

-- 添加字段注释
COMMENT ON COLUMN retrieval_test_record.id IS '主键ID';
COMMENT ON COLUMN retrieval_test_record.document_id IS '文档ID';
COMMENT ON COLUMN retrieval_test_record.query IS '检索查询内容';
COMMENT ON COLUMN retrieval_test_record.create_time IS '创建时间';
