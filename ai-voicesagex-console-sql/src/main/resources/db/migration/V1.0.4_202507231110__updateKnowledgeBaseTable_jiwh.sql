alter table public.knowledge_base
    add enable_qa_chunk boolean;

update menu set menu_name = '知识库' where menu_name = 'RAG知识库';
