package cn.voicecomm.ai.voicesagex.console.api.dto.application;

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
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 存储文件信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFilesResp implements Serializable {

    /**
     * 文件的唯一标识符。
     */
    private Integer id;

    /**
     * 文件名。
     */
    private String name;

    /**
     * 文件大小（字节）。
     */
    private Long size;

    /**
     * 文件扩展名。
     */
    private String extension;

    /**
     * MIME 类型。
     */
    private String mime_type;

    /**
     * 创建者ID。
     */
    private Integer createBy;

    /**
     * 创建时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 预览URL。
     */
    private String preview_url;
}



