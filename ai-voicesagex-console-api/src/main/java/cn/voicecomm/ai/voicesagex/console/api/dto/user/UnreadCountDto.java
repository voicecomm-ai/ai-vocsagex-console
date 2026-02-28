package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnreadCountDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -5501378842056364814L;

  private Long total;

}
