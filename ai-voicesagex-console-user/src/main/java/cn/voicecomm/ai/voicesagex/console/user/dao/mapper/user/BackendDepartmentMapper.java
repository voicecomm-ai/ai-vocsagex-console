package cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user;

import cn.voicecomm.ai.voicesagex.console.util.po.user.DepartmentPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BackendDepartmentMapper extends BaseMapper<DepartmentPo> {

  List<DepartmentPo> listChildDept(@Param("id") Integer id,
    @Param("includeSelf") Integer includeSelf);

  List<DepartmentPo> listParentDept(@Param("id") Integer id,
    @Param("includeSelf") Integer includeSelf);
}
