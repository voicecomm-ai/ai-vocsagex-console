package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphVectorInformationService;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphVectorInformationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphVectorInformationPo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 图知识库向量信息接口实现类
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphVectorInformationServiceImpl extends
    ServiceImpl<KnowledgeGraphVectorInformationMapper, KnowledgeGraphVectorInformationPo> implements
    KnowledgeGraphVectorInformationService {

  @Override
  public void saveVectorJobInfo(String job, Integer spaceId) {
    log.info("【Mysql save create job info : {}】", job);
    KnowledgeGraphVectorInformationPo vectorInformation = KnowledgeGraphVectorInformationPo.builder()
        //时间戳作为id
        .vectorJobId(job).spaceId(spaceId).build();
    baseMapper.insert(vectorInformation);
  }

  @Override
  public void deleteVectorJobInfo(String job) {
    log.info("【Mysql delete  job info : {}】", job);
    int result = baseMapper.deleteById(job);
    log.info("【Mysql delete  job info success  : {} ,执行结果： {}】", job, result);
  }
}
