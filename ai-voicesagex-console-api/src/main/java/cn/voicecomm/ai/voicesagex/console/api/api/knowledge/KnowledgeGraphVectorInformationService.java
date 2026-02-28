package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

/**
 * 图知识库向量信息Service
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
public interface KnowledgeGraphVectorInformationService {

  void saveVectorJobInfo(String job, Integer spaceId);

  void deleteVectorJobInfo(String job);

}

