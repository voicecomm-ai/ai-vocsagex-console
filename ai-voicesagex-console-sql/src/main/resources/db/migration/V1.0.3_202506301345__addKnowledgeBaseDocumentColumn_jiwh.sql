alter table knowledge_base_document
    add word_count int8;

comment on column knowledge_base_document.word_count is '文档字符数';

alter table knowledge_base
    add enable_hybrid_search_rerank boolean;
comment on column knowledge_base.enable_hybrid_search_rerank is '混合检索是否启用Rerank模型';
