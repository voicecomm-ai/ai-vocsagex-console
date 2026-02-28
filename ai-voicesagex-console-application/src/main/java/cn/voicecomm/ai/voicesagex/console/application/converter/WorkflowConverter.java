package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunsDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.WorkflowRunsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * WorkflowConverter
 *
 * @author wangfan
 * @date 2025/4/1 下午 3:39
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WorkflowConverter {

  WorkflowPo publishPoToWorkflow(WorkflowsPublishHistoryPo po);

  WorkflowPo experiencePoToWorkflow(WorkflowsExperiencePo po);

  List<WorkflowNodeExecutionsDto> nodeExecutionPoToDto(List<WorkflowNodeExecutionsPo> po);

  default WorkflowDto poToDto(WorkflowPo po) {
    if (po == null) {
      return null;
    }

    WorkflowDto workflowDto = new WorkflowDto();

    workflowDto.setCreateTime(po.getCreateTime());
    workflowDto.setUpdateTime(po.getUpdateTime());
    workflowDto.setCreateBy(po.getCreateBy());
    workflowDto.setUpdateBy(po.getUpdateBy());
    workflowDto.setId(po.getId());
    workflowDto.setTenant_id(po.getTenant_id());
    workflowDto.setApp_id(po.getApp_id());
    workflowDto.setType(po.getType());
    workflowDto.setVersion(po.getVersion());
    workflowDto.setMarked_name(po.getMarked_name());
    workflowDto.setMarked_comment(po.getMarked_comment());
    workflowDto.setGraph(JSONUtil.parseObj(po.getGraph()));
    workflowDto.setFeatures(JSONUtil.parseObj(po.getFeatures()));
    workflowDto.setEnvironment_variables(JSONUtil.parseArray(po.getEnvironment_variables()));
    workflowDto.setConversation_variables(JSONUtil.parseObj(po.getConversation_variables()));
    return workflowDto;
  }

  default WorkflowPo dtoToPo(WorkflowDto po) {
    if (po == null) {
      return null;
    }

    WorkflowPo workflowPo = new WorkflowPo();

    workflowPo.setCreateTime(po.getCreateTime());
    workflowPo.setUpdateTime(po.getUpdateTime());
    workflowPo.setCreateBy(po.getCreateBy());
    workflowPo.setUpdateBy(po.getUpdateBy());
    workflowPo.setId(po.getId());
    workflowPo.setTenant_id(po.getTenant_id());
    workflowPo.setApp_id(po.getApp_id());
    workflowPo.setType(po.getType());
    workflowPo.setVersion(po.getVersion());
    workflowPo.setMarked_name(po.getMarked_name());
    workflowPo.setMarked_comment(po.getMarked_comment());
    workflowPo.setGraph(JSONUtil.toJsonStr(po.getGraph()));
    workflowPo.setFeatures(JSONUtil.toJsonStr(po.getFeatures()));
    workflowPo.setEnvironment_variables(JSONUtil.toJsonStr(po.getEnvironment_variables()));
    workflowPo.setConversation_variables(JSONUtil.toJsonStr(po.getConversation_variables()));

    return workflowPo;
  }


  default WorkflowInfoResponseDto poToInfoDto(WorkflowPo po) {
    if (po == null) {
      return null;
    }

    WorkflowInfoResponseDto workflowDto = new WorkflowInfoResponseDto();

    workflowDto.setCreateTime(po.getCreateTime());
    workflowDto.setUpdateTime(po.getUpdateTime());
    workflowDto.setCreateBy(po.getCreateBy());
    workflowDto.setUpdateBy(po.getUpdateBy());
    workflowDto.setId(po.getId());
    workflowDto.setTenant_id(po.getTenant_id());
    workflowDto.setApp_id(po.getApp_id());
    workflowDto.setType(po.getType());
    workflowDto.setVersion(po.getVersion());
    workflowDto.setMarked_name(po.getMarked_name());
    workflowDto.setMarked_comment(po.getMarked_comment());
    workflowDto.setGraph(JacksonUtil.readTree(po.getGraph()));
    workflowDto.setFeatures(JSONUtil.parseObj(po.getFeatures()));
    workflowDto.setEnvironment_variables(JSONUtil.parseArray(po.getEnvironment_variables()));
    workflowDto.setConversation_variables(JSONUtil.parseObj(po.getConversation_variables()));
    return workflowDto;
  }

  default WorkflowInfoResponseDto poToInfoDto(WorkflowsExperiencePo po) {
    if (po == null) {
      return null;
    }

    WorkflowInfoResponseDto workflowDto = new WorkflowInfoResponseDto();

    workflowDto.setCreateTime(po.getCreate_time());
    workflowDto.setCreateBy(po.getCreate_by());
    workflowDto.setId(po.getId());
    workflowDto.setApp_id(po.getApp_id());
    workflowDto.setType(po.getType());
    workflowDto.setGraph(JacksonUtil.readTree(po.getGraph()));
    workflowDto.setFeatures(JSONUtil.parseObj(po.getFeatures()));
    workflowDto.setEnvironment_variables(JSONUtil.parseArray(po.getEnvironment_variables()));
    workflowDto.setConversation_variables(JSONUtil.parseObj(po.getConversation_variables()));
    return workflowDto;
  }

  /**
   * 将 WorkflowNodeExecutionsDto 转换为 WorkflowNodeExecutionsPo 注意：createTime 由 MyBatis Plus 自动填充，不从
   * DTO 设置
   *
   * @param po 数据传输对象
   * @return 持久化对象
   */
//  default WorkflowNodeExecutionsPo nodeExecutionDtoToPo(WorkflowNodeExecutionsDto dto) {
//    if (dto == null) {
//      return null;
//    }
//
//    return new WorkflowNodeExecutionsPo()
//        .setId(dto.getId())
//        .setTenant_id(dto.getTenant_id())
//        .setApp_id(dto.getApp_id())
//        .setWorkflow_id(dto.getWorkflow_id())
//        .setTriggered_from(dto.getTriggered_from())
//        .setWorkflow_run_id(dto.getWorkflow_run_id())
//        .setIndex(dto.getIndex())
//        .setPredecessor_node_id(dto.getPredecessor_node_id())
//        .setNode_execution_id(dto.getNode_execution_id())
//        .setNode_id(dto.getNode_id())
//        .setNode_type(dto.getNode_type())
//        .setTitle(dto.getTitle())
//        .setInputs(JSONUtil.toJsonStr(dto.getInputs()))
//        .setProcess_data(JSONUtil.toJsonStr(dto.getProcess_data()))
//        .setOutputs(JSONUtil.toJsonStr(dto.getOutputs()))
//        .setStatus(dto.getStatus())
//        .setError(dto.getError())
//        .setElapsed_time(dto.getElapsed_time())
//        .setExecution_metadata(JSONUtil.toJsonStr(dto.getExecution_metadata()))
//        .setFinished_at(dto.getFinished_at())
//        .setCreated_by_role(dto.getCreated_by_role())
//        .setCreatedBy(dto.getCreatedBy());
//  }
  default WorkflowNodeExecutionsDto nodeExecutionPoToDto(WorkflowNodeExecutionsPo po) {
    if (po == null) {
      return null;
    }
    return new WorkflowNodeExecutionsDto()
        .setId(po.getId())
        .setTenant_id(po.getTenant_id())
        .setApp_id(po.getApp_id())
        .setWorkflow_id(po.getWorkflow_id())
        .setTriggered_from(po.getTriggered_from())
        .setWorkflow_run_id(po.getWorkflow_run_id())
        .setIndex(po.getIndex())
        .setPredecessor_node_id(po.getPredecessor_node_id())
        .setNode_execution_id(po.getNode_execution_id())
        .setNode_id(po.getNode_id())
        .setNode_type(po.getNode_type())
        .setTitle(po.getTitle())
        .setInputs(JacksonUtil.readTree(po.getInputs()))
        .setProcess_data(JacksonUtil.readTree(po.getProcess_data()))
        .setOutputs(JacksonUtil.readTree(po.getOutputs()))
        .setStatus(po.getStatus())
        .setError(po.getError())
        .setElapsed_time(po.getElapsed_time())
        .setExecution_metadata(JacksonUtil.readTree(po.getExecution_metadata()))
        .setFinished_at(po.getFinished_at())
        .setLoop_index(po.getLoop_index())
        .setCreated_by_role(po.getCreated_by_role())
        .setCreatedBy(po.getCreatedBy())
        .setToolAppId(po.getToolAppId())
        .setCreateTime(po.getCreateTime());
  }


  default WorkflowRunsDto workflowRunPoToDto(WorkflowRunsPo po) {
    if (po == null) {
      return null;
    }
    return new WorkflowRunsDto()
        .setId(po.getId())
        .setWorkflow_run_id(po.getWorkflow_run_id())
        .setTenant_id(po.getTenant_id())
        .setApp_id(po.getApp_id())
        .setWorkflow_id(po.getWorkflow_id())
        .setType(po.getType())
        .setTriggered_from(po.getTriggered_from())
        .setVersion(po.getVersion())
        .setGraph(JacksonUtil.readTree(po.getGraph()))
        .setInputs(JacksonUtil.readTree(po.getInputs()))
        .setStatus(po.getStatus())
        .setOutputs(JacksonUtil.readTree(po.getOutputs()))
        .setError(po.getError())
        .setElapsed_time(po.getElapsed_time())
        .setTotal_tokens(po.getTotal_tokens())
        .setTotal_steps(po.getTotal_steps())
        .setExceptions_count(po.getExceptions_count())
        .setCreated_by(po.getCreated_by())
        .setCreated_at(po.getCreated_at())
        .setFinished_at(po.getFinished_at());
  }
}
