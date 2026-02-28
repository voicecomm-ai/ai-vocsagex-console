package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件保存请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileParseReq {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件格式
     */
    private String fileType;

    /**
     * mataData
     */
    private JSONObject metadata = new JSONObject();

    /**
     * 回调接口
     */
    private String callbackUrl;

    /**
     * 文件继续chunk回调大小 默认 50
     */
    private Integer callbackChunkSize = 50 ;


    private String spaceId;


    private String documentId;


    public FileParseReq(String filePath, String fileType, String callbackUrl, String spaceId) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.callbackUrl = callbackUrl;
        this.spaceId = spaceId;
    }



    public FileParseReq(String filePath, String fileType, String callbackUrl) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.callbackUrl = callbackUrl;

    }
}
