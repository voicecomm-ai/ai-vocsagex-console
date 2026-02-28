alter table knowledge_base
    drop column common_segment_identifier;

alter table knowledge_base
    drop column common_max_segment_length;

alter table knowledge_base
    drop column common_segment_overlap_length;

alter table knowledge_base
    drop column enable_qa_chunking;

alter table knowledge_base
    drop column qa_model_id;

alter table knowledge_base
    drop column enable_common_text_preprocessing_rule1;

alter table knowledge_base
    drop column enable_common_text_preprocessing_rule2;

alter table knowledge_base
    drop column parent_chunk_context;

alter table knowledge_base
    drop column child_chunk_segment_identifier;

alter table knowledge_base
    drop column child_chunk_max_segment_length;

alter table knowledge_base
    drop column enable_parent_child_text_preprocessing_rule1;

alter table knowledge_base
    drop column enable_parent_child_text_preprocessing_rule2;

alter table knowledge_base
    drop column parent_chunk_paragraph_segment_identifier;

alter table knowledge_base
    drop column parent_chunk_paragraph_max_segment_length;

