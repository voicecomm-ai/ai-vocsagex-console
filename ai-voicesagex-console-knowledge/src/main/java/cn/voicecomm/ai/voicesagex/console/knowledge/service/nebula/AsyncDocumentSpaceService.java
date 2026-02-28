package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import java.util.concurrent.CompletableFuture;

public interface AsyncDocumentSpaceService {


  CompletableFuture<Void> makeAsyncRequest(GraphCreateSpace graphCreateSpace);

}
