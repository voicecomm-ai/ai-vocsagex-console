ALTER TABLE public.knowledge_document_information
ALTER COLUMN job_id TYPE VARCHAR(255) USING job_id::VARCHAR(255),
ALTER COLUMN job_id SET NOT NULL,
ALTER COLUMN job_id SET DEFAULT '';
