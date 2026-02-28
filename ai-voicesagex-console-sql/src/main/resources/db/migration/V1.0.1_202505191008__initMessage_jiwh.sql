DROP TABLE IF EXISTS message;
CREATE TABLE message (
    id              SERIAL PRIMARY KEY,
    user_id         INTEGER NOT NULL DEFAULT 0,
    is_read         boolean NOT NULL DEFAULT false,
    type            INTEGER NOT NULL DEFAULT 0,
    msg             VARCHAR(255) NOT NULL DEFAULT '',
    msg_type        INTEGER NOT NULL DEFAULT 0,
    attachment      jsonb,
    download_path   VARCHAR(255) NOT NULL DEFAULT '',
    deleted         boolean NOT NULL DEFAULT false,
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加字段注释（可选）
COMMENT ON TABLE message IS '消息表';
COMMENT ON COLUMN message.id IS '主键id';
COMMENT ON COLUMN message.user_id IS '用户id';
COMMENT ON COLUMN message.is_read IS '0：未读 1：已读';
COMMENT ON COLUMN message.type IS '0：成功 1：失败 2：提醒 3：通知';
COMMENT ON COLUMN message.msg IS '消息';
COMMENT ON COLUMN message.msg_type IS '消息文本类型';
COMMENT ON COLUMN message.attachment IS '消息附加信息';
COMMENT ON COLUMN message.download_path IS '下载路径';
COMMENT ON COLUMN message.deleted IS '是否删除 0 否 1 删除';
COMMENT ON COLUMN message.create_time IS '创建时间';
COMMENT ON COLUMN message.update_time IS '修改时间';
