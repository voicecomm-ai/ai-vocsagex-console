package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BackendMenuListDto extends BaseDto {

  private List<BackendMenuDto> menuList;

  private List<String> uriList;
}
