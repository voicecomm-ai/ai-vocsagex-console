package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

/**
 * 运行时路由状态类
 * 管理工作流执行过程中所有节点的状态和路由关系
 */
@Data
public class RuntimeRouteState {
    
    /**
     * 图状态路由 (source_node_state_id: target_node_state_id)
     */
    private Map<String, List<String>> routes = new ConcurrentHashMap<>();
    
    /**
     * 节点状态映射 (route_node_state_id: route_node_state)
     */
    private Map<String, RouteNodeState> node_state_mapping = new ConcurrentHashMap<>();
    
    /**
     * 创建节点状态
     * 
     * @param node_id 节点ID
     * @return 路由节点状态
     */
    public RouteNodeState createNodeState(String node_id) {
        // 创建新的节点状态对象
        RouteNodeState state = new RouteNodeState();
        state.setNode_id(node_id);
        state.setStart_at(LocalDateTime.now());
        state.setId(UUID.randomUUID().toString());
        
        // 将节点状态添加到映射中
        this.node_state_mapping.put(state.getId(), state);
        return state;
    }
    
    /**
     * 添加路由到图状态
     * 
     * @param source_node_state_id 源节点状态ID
     * @param target_node_state_id 目标节点状态ID
     */
    public void addRoute(String source_node_state_id, String target_node_state_id) {
        // 如果源节点状态ID不存在，初始化列表
        if (!this.routes.containsKey(source_node_state_id)) {
            this.routes.put(source_node_state_id, new ArrayList<>());
        }
        
        // 添加路由关系
        this.routes.get(source_node_state_id).add(target_node_state_id);
    }
    
    /**
     * 根据源节点状态ID获取路由和节点状态
     * 
     * @param source_node_state_id 源节点状态ID
     * @return 路由节点状态列表
     */
    public List<RouteNodeState> getRoutesWithNodeStateBySourceNodeStateId(String source_node_state_id) {
        List<String> target_state_ids = this.routes.get(source_node_state_id);
        List<RouteNodeState> result = new ArrayList<>();
        
        if (target_state_ids != null) {
            for (String target_state_id : target_state_ids) {
                RouteNodeState state = this.node_state_mapping.get(target_state_id);
                if (state != null) {
                    result.add(state);
                }
            }
        }
        
        return result;
    }
}
