package cn.voicecomm.ai.voicesagex.console.util.po.application.workflow;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "upload_files")
public class UploadFilesPo implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    private String tenant_id;

    /**
     * 存储类型
     */
    @TableField(value = "storage_type")
    private String storage_type;

    /**
     * 存储键值，在指定存储中的唯一路径或标识符
     */
    @TableField(value = "\"key\"")
    private String key;

    /**
     * 文件原始名称（包含扩展名）
     */
    @TableField(value = "\"name\"")
    private String name;

    /**
     * 大小
     */
    @TableField(value = "\"size\"")
    private Integer size;

    /**
     * 文件扩展名
     */
    @TableField(value = "extension")
    private String extension;

    /**
     * 文件MIME类型（例如 'image/jpeg', 'application/pdf'）
     */
    @TableField(value = "mime_type")
    private String mime_type;

    /**
     * 是否已被使用
     */
    @TableField(value = "used")
    private Boolean used;

    /**
     * 使用者ID
     */
    @TableField(value = "used_by")
    private Integer used_by;

    /**
     * 使用时间
     */
    @TableField(value = "used_at")
    private LocalDateTime used_at;

    /**
     * 文件内容哈希值（例如 MD5, SHA1, SHA256），用于校验和去重
     */
    @TableField(value = "hash")
    private String hash;

    /**
     * 文件来源URL（如果是从外部URL下载上传的）
     */
    @TableField(value = "source_url")
    private String source_url;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    /**
     * 创建人id
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Integer createBy;
}