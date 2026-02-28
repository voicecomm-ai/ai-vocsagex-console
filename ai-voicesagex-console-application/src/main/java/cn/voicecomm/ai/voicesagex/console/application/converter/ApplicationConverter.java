package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationKeyDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * ApplicationConverter
 *
 * @author wangfan
 * @date 2025/4/1 下午 3:39
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ApplicationConverter {

  ApplicationDto poToDto(ApplicationPo po);

  ApplicationPo dtoToPo(ApplicationDto po);

  List<ApplicationPo> dtoToPoList(List<ApplicationDto> obj);

  List<ApplicationDto> poToDtoList(List<ApplicationPo> obj);


  PagingRespDto<ApplicationDto> poToDtoPageList(Page<ApplicationPo> obj);

  default ApplicationExperiencePo appPoToExePo(ApplicationPo po) {
    if (po == null) {
      return null;
    }

    ApplicationExperiencePo applicationExperiencePo = new ApplicationExperiencePo();
    applicationExperiencePo.setCreateTime(po.getCreateTime());
    applicationExperiencePo.setCreateBy(po.getCreateBy());
    applicationExperiencePo.setId(po.getId());
    applicationExperiencePo.setType(po.getType());
    applicationExperiencePo.setName(po.getName());
    applicationExperiencePo.setDescription(po.getDescription());
    applicationExperiencePo.setIconUrl(po.getIconUrl());
    applicationExperiencePo.setAppId(po.getId());
    applicationExperiencePo.setAgentType(po.getAgentType());

    return applicationExperiencePo;
  }

  default ApplicationExperiencePo appDtoToExePo(ApplicationDto po) {
    if (po == null) {
      return null;
    }

    ApplicationExperiencePo applicationExperiencePo = new ApplicationExperiencePo();
    applicationExperiencePo.setCreateTime(po.getCreateTime());
    applicationExperiencePo.setCreateBy(po.getCreateBy());
    applicationExperiencePo.setId(po.getId());
    applicationExperiencePo.setType(po.getType());
    applicationExperiencePo.setName(po.getName());
    applicationExperiencePo.setDescription(po.getDescription());
    applicationExperiencePo.setIconUrl(po.getIconUrl());
    applicationExperiencePo.setAppId(po.getId());
    applicationExperiencePo.setAgentType(po.getAgentType());

    return applicationExperiencePo;
  }


  List<ApplicationExperienceDto> exePoToDtoList(List<ApplicationExperiencePo> po);

  List<ApplicationExperienceTagDto> tagPoToDtoList(List<ApplicationExperienceTagPo> po);

  ApplicationExperienceTagDto tagPoToDto(ApplicationExperienceTagPo po);


  ApplicationKeyDto appKeyPoToDto(ApplicationKeyPo po);

  List<ApplicationKeyDto> appKeyPoToDtoList(List<ApplicationKeyPo> po);
}
