package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphVertexDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.BatchDocumentInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.BatchVerificationInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.VerificationInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Set;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

/**
 * 实体节点
 */

public interface GraphVertexMapper  extends NebulaDaoBasic<GraphCreateTagEdge,String> {
    void deleteVertex(@Param("tex") GraphVertexDropDO tex);


    void  deleteVertexAll( @Param("spaceId")String spaceId,  @Param("ids")String ids);
    void  deleteVertexSingle( @Param("spaceId")String spaceId,  @Param("id")String id);

    void saveEntity(@Param("tex") GraphEntityDO tex);
    void saveEntityImport(@Param("tex") GraphEntityDO tex);

    void saveEntityBath(@Param("tex") GraphEntityDO tex);

    void updateEntity(@Param("tex") GraphEntityDO tex);


    void saveSubject(@Param("tex") GraphRelationEntityDO tex);
    void saveSubjectNoProperty(@Param("tex")GraphRelationEntityDO tex);

    void saveObject(@Param("tex")GraphRelationEntityDO tex);
    void saveObjectNoProperty(@Param("tex")GraphRelationEntityDO tex);


    NgVertex getEntity(@Param("entityId")String entityId, @Param("spaceId")String spaceId);
    List<NgVertex<String>> getEntitySet(@Param("entitys") Set<String> entitys, @Param("spaceId")String spaceId);

    List<NgVertex<String>> getNgvertexs( @Param("spaceId")String spaceId);
    List<NgVertex<String>> getVertexesByTagName( @Param("spaceId")String spaceId,@Param("tagName") String tagName,@Param("entityName")String entityName);
    List<NgVertex<String>> getSelectNgvertexs( @Param("spaceId")String spaceId,@Param("ids") List<String> ids);

    List<NgVertex<String>> getAllNgvertexs(@Param("tex")GraphEntityDO graphEntityDO);


    List<NgVertex<String>> getAllNgvertexsByName(@Param("tex")GraphEntityDO graphEntityDO);

    Integer getNumber(@Param("spaceId")String spaceId,@Param("tagName")String tagName );


    ResultSet getShowStuta(@Param("spaceId")String spaceId);

    ResultSet selectLikeEntity(@Param("spaceId")String spaceId,@Param("tagName") String tagName,@Param("entityName") String entityName);


    NgVertex<String> getVertexLimit(@Param("spaceId")String spaceId);

    Integer getEntityTotal(@Param("tex")GraphEntityDO graphEntityDO);
    Integer getEntityTotalByTagName(@Param("tex")GraphEntityDO graphEntityDO);

    List<NgVertex<String>> getNgvertexsExport( @Param("spaceId")String spaceId, @Param("entityName")String entityName);
    List<NgVertex<String>> getNgvertexsByName( @Param("spaceId")String spaceId,@Param("tagName")String tagName ,@Param("entityName")String entityName);
    ResultSet getEntityTotalByProperty( @Param("spaceId")String spaceId, @Param("tagName")String tagName,@Param("propertyName") String propertyName);
    ResultSet getEntityTotalByAllProperty( @Param("spaceId")String spaceId, @Param("tagName")String tagName);

    List<NgVertex<String>> getVertexesByTagNameExport(@Param("spaceId")String spaceId, @Param("tagName")String tagName,@Param("entityName") String entityName);

    void saveEntityFusion(@Param("tex") GraphEntityDO graphEntityDO);


   void saveEntityVerification(@Param("tex") VerificationInfo tex);

   void batchSaveEntityVerification(@Param("tex") BatchVerificationInfo tex);

   void batchSaveEntityDocument(@Param("tex") BatchDocumentInfo tex);

    Integer executeTheTask(@Param("spaceId") String spaceId);

    ResultSet getStatsInfo(@Param("spaceId")String s);

    Integer getEntityTotalBySpaceId(@Param("spaceId")String spaceId);

    String getDocumentName(@Param("spaceId")String spaceId,@Param("edgeName")String edgeName,@Param("value")String value);

}
