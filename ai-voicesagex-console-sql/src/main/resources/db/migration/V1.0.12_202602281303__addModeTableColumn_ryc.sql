ALTER TABLE "public"."model"
    ADD COLUMN "api_key" varchar(512) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying;

COMMENT
ON COLUMN "public"."model"."api_key" IS '模型apiKey';


delete
from menu
where sign in
      ('modelMyModel', 'algorithmModel', 'algorithmModelView', 'algorithmModelOperation',
       'modelFineTuning', 'algorithmEvaluation', 'preEvaluation');