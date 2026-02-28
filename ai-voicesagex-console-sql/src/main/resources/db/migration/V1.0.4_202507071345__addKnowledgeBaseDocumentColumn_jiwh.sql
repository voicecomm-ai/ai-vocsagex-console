alter table knowledge_base_document
    add status varchar(32) default 'DISABLE' not null;

comment on column knowledge_base_document.status is '文档状态 ENABLE DISABLE ARCHIVE';


alter table knowledge_base_document
    add process_failed_reason varchar(256);

comment on column knowledge_base_document.process_failed_reason is '文档处理失败原因';


alter table knowledge_base_document add "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table knowledge_base_document add "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;

