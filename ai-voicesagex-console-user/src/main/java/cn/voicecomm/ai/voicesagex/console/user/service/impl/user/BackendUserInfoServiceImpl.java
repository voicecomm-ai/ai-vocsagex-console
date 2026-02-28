package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserInfoService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendUserMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.user.SysUserPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class BackendUserInfoServiceImpl extends
  ServiceImpl<BackendUserMapper, SysUserPo> implements
  BackendUserInfoService {

  @Override
  public CommonRespDto<Map<Integer, String>> getUserNameMapByUserIds(List<Integer> userIds) {
    if (CollUtil.isEmpty(userIds)) {
      return CommonRespDto.success(new HashMap<>());
    }

    Map<Integer, String> map = baseMapper.selectBatchIds(userIds).stream()
      .collect(Collectors.toMap(SysUserPo::getId, SysUserPo::getAccount));
    return CommonRespDto.success(map);
  }

  @Override
  public CommonRespDto<Map<Integer, String>> getAccountMapByUserIds(List<Integer> userIds) {

    Map<Integer, String> map = baseMapper.selectBatchIds(userIds).stream()
      .collect(Collectors.toMap(SysUserPo::getId, SysUserPo::getAccount));
    return CommonRespDto.success(map);
  }

  @Override
  public CommonRespDto<Map<Integer, String>> getNameMapByUserIds(List<Integer> userIds) {
    if (CollUtil.isEmpty(userIds)) {
      return CommonRespDto.success(new HashMap<>());
    }

    Map<Integer, String> map = baseMapper.selectBatchIds(userIds).stream()
      .collect(Collectors.toMap(SysUserPo::getId, SysUserPo::getUsername));
    return CommonRespDto.success(map);
  }

  @Override
  public CommonRespDto<Map<Integer, String>> getUserNameAndAccountMapByUserIds(
    List<Integer> userIds) {
    Map<Integer, String> map = baseMapper.selectList(Wrappers.<SysUserPo>lambdaQuery()
        .in(CollUtil.isNotEmpty(userIds), SysUserPo::getId, userIds)).stream()
      .collect(Collectors.toMap(SysUserPo::getId,
        po -> po.getUsername() + "（" + po.getAccount()
          + "）"));
    return CommonRespDto.success(map);
  }
}
