package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型可视化对象
 */
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphPattern implements Serializable {


    @Serial
    private static final long serialVersionUID = -5295337951765671030L;

    private Set<Data> nodes;

    private Set<Link> edges;

}
