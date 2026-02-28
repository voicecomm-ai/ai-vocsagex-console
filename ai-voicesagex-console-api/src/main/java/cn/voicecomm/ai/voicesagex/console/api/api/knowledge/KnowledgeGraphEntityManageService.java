package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDetailsVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityInfosVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveEntityVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalTagInfosVO;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 实体Service
 *
 * @Author ryc
 * @Date 2025/9/8 15:04
 */
public interface KnowledgeGraphEntityManageService {

  /**
   * 获取本体列表
   *
   * @return
   */
  CommonRespDto<TotalTagInfosVO> getTagInfoList(String spaceId, String tagName);


  /**
   * 获取本体属性详细信息
   *
   * @param
   * @return
   */
  CommonRespDto<List<TagPropertyResultVO>> getTagProperties(List<Long> tagIds);


  CommonRespDto<PagingRespDto<EntityInfosVO>> getEntities(TagPropertyVO tagPropertyVO);

  /**
   * 删除实体记录
   *
   * @param tagDeleteVO
   */
  CommonRespDto<Boolean> deleteVertex(TagDeleteVO tagDeleteVO);

  /**
   * 删除实体记录
   *
   * @param
   */
  CommonRespDto<Boolean> deleteAllVertex(EntityDropAllVO entityDropAllVO);


  /**
   * 新增实体
   *
   * @param saveEntityVO
   */
  CommonRespDto<List<SaveEntityVO>> saveEntity(List<SaveEntityVO> saveEntityVO);


  /**
   * 获取实体
   */
  CommonRespDto<EntityDetailsVO> getEntity(SaveEntityVO saveEntityVO);

  /**
   * 编辑实体
   *
   * @param
   */
  CommonRespDto<Boolean> updateEntity(EntityDetailsVO entityDetailsVO);

  CommonRespDto<List<EntityLikeInfoVO>> selectLikeEntity(EntityLikeVO entityLikeVO);


  String processData(String propertyType, String propertyValue);


  CommonRespDto<Boolean> checkDataUp(Long spaceId);


  boolean checkDataLimit(Long spaceId);


  Integer getTotalDataUpBySpace(Long spaceId) throws UnsupportedEncodingException;

  Integer getResourceLimitData(Long spaceId);

  boolean checkDataLimitData(Long spaceId, int i);
}
