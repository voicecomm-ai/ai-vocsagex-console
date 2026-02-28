alter table public.retrieval_test_record
    rename column document_id to knowledge_base_id;

comment on column public.retrieval_test_record.knowledge_base_id is '知识库ID';

