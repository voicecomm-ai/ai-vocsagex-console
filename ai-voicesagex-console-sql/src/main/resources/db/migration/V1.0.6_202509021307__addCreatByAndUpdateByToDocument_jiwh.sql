ALTER TABLE "public"."knowledge_base_document" ADD COLUMN create_by integer NOT NULL DEFAULT 1;
ALTER TABLE "public"."knowledge_base_document" ADD COLUMN update_by integer NOT NULL DEFAULT 1;
