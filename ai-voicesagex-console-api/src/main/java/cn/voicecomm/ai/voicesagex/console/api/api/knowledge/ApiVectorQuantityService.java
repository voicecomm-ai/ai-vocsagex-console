package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.DeleteVectorDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveEntityDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveRelationDTO;
import java.util.concurrent.CompletableFuture;

public interface ApiVectorQuantityService {

  CompletableFuture<Void> makeAsyncCreateOrDropCollection(String collection, Integer type);

  CompletableFuture<Void> makeAsyncCreateEntity(SaveEntityDTO saveEntityDTO);

  CompletableFuture<Void> makeAsyncCreateRelation(SaveRelationDTO saveRelationDTO);

  CompletableFuture<Void> makeAsyncDeleteRelationEntity(DeleteVectorDTO deleteVector);

  CompletableFuture<Void> makeAsyncDelete(DeleteVectorDTO deleteVector);

}
