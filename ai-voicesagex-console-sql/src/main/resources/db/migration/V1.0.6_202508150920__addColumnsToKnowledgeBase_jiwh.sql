alter table public.knowledge_base
    add graph_search_rerank_model_id integer;

comment on column public.knowledge_base.graph_search_rerank_model_id is '知识图谱检索Rerank模型ID';

alter table public.knowledge_base
    add graph_search_top_k integer;

comment on column public.knowledge_base.graph_search_top_k is '知识图谱检索top k';

alter table public.knowledge_base
    add enable_graph_search_rerank boolean;

comment on column public.knowledge_base.enable_graph_search_rerank is '是否启用知识图谱检索Rerank';

