comment on column public.knowledge_base_document.status is '文档状态 ENABLE DISABLE';

alter table public.knowledge_base_document
    alter column status set default 'ENABLE'::character varying;

alter table public.knowledge_base_document
    add is_archived boolean default false not null;
