package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseMetadataService
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataBaseDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataWithValueDto
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.BuiltInMetadata
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.MetadataType
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseDocumentMapper
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseDocumentMetadataMapper
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseMetadataMapper
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseDocumentMetadataPo
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseDocumentPo
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseMetadataPo
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import org.apache.dubbo.config.annotation.DubboReference
import org.apache.dubbo.config.annotation.DubboService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

@Service
@DubboService
class KnowledgeBaseMetadataServiceImpl(
    private val knowledgeBaseMetadataMapper: KnowledgeBaseMetadataMapper,
    private val knowledgeBaseDocumentMetadataMapper: KnowledgeBaseDocumentMetadataMapper,
    private val knowledgeBaseDocumentMapper: KnowledgeBaseDocumentMapper,
) : KnowledgeBaseMetadataService {
    @DubboReference
    lateinit var backendUserService: BackendUserService

    private val log: Logger = LoggerFactory.getLogger(KnowledgeBaseMetadataServiceImpl::class.java)

    // 元数据名称验证正则表达式
    private val metadataNamePattern = Pattern.compile("^[a-z][a-z0-9_]*$")

    // 时间格式验证器
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    /**
     * 验证元数据名称是否符合规则
     */
    private fun validateMetadataName(name: String): Boolean = name.length <= 50 && metadataNamePattern.matcher(name).matches()

    /**
     * 验证元数据值是否符合类型要求
     */
    private fun validateMetadataValue(
        value: String,
        type: MetadataType,
    ): Boolean =
        when (type) {
            MetadataType.String -> true // String类型接受任何字符串
            MetadataType.Number -> {
                try {
                    value.toDouble()
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            }
            MetadataType.Time -> {
                try {
                    timeFormatter.parse(value)
                    true
                } catch (e: DateTimeParseException) {
                    false
                }
            }
        }

    override fun enableBuiltInMetadata(knowledgeBaseId: Int): CommonRespDto<Void> {
        // 获取知识库下的所有文档ID
        val documents =
            knowledgeBaseDocumentMapper
                .selectList(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .select(
                            KnowledgeBaseDocumentPo::id,
                            KnowledgeBaseDocumentPo::updateTime,
                            KnowledgeBaseDocumentPo::name,
                            KnowledgeBaseDocumentPo::createTime,
                            KnowledgeBaseDocumentPo::createBy,
                        ).eq(KnowledgeBaseDocumentPo::knowledgeBaseId, knowledgeBaseId),
                )

        BuiltInMetadata.entries.forEach { builtInMetadata ->
            val metadata =
                knowledgeBaseMetadataMapper.selectOne(
                    KtQueryWrapper(
                        KnowledgeBaseMetadataPo(),
                    ).eq(
                        KnowledgeBaseMetadataPo::name,
                        builtInMetadata.name,
                    ).eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId)
                        .eq(KnowledgeBaseMetadataPo::isBuiltIn, true),
                )

            if (metadata == null) {
                log.info("Add built in metadata ${builtInMetadata.name} to knowledge base $knowledgeBaseId")
                val newMetadata =
                    KnowledgeBaseMetadataPo().apply {
                        name = builtInMetadata.name
                        type = builtInMetadata.type.name
                        isBuiltIn = true
                        this.knowledgeBaseId = knowledgeBaseId
                        createTime = LocalDateTime.now()
                        updateTime = LocalDateTime.now()
                    }
                knowledgeBaseMetadataMapper.insert(newMetadata)

                // 为所有文档绑定内置元数据
                documents.forEach { document ->
                    val uploader = backendUserService.getUserInfo(document.createBy).data
                    val relation =
                        KnowledgeBaseDocumentMetadataPo().apply {
                            metadataId = newMetadata.id
                            this.documentId = document.id
                            name = newMetadata.name
                            createTime = LocalDateTime.now()
                            updateTime = LocalDateTime.now()
                            value =
                                when (builtInMetadata) {
                                    BuiltInMetadata.document_name -> document.name
                                    BuiltInMetadata.last_update_date ->
                                        document.updateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                    BuiltInMetadata.upload_date ->
                                        document.createTime?.format(
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                                        )
                                    BuiltInMetadata.source -> "file_upload"
                                    BuiltInMetadata.uploader -> uploader?.account ?: ""
                                    else -> ""
                                }
                        }
                    knowledgeBaseDocumentMetadataMapper.insert(relation)
                }

                log.info(
                    "Bound built in metadata ${builtInMetadata.name} to ${documents.size} documents in knowledge base $knowledgeBaseId",
                )
            } else {
                log.info("Built in metadata ${builtInMetadata.name} already exists in knowledge base $knowledgeBaseId")

                // 检查是否已为所有文档绑定该元数据
                documents.forEach { document ->
                    val existingRelation =
                        knowledgeBaseDocumentMetadataMapper.selectOne(
                            KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                                .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadata.id)
                                .eq(KnowledgeBaseDocumentMetadataPo::documentId, document.id),
                        )

                    if (existingRelation == null) {
                        // 为未绑定的文档添加绑定关系
                        val uploader = backendUserService.getUserInfo(document.createBy).data
                        val relation =
                            KnowledgeBaseDocumentMetadataPo().apply {
                                this.metadataId = metadata.id
                                this.documentId = document.id
                                this.name = metadata.name
                                createTime = LocalDateTime.now()
                                updateTime = LocalDateTime.now()
                                value =
                                    when (builtInMetadata) {
                                        BuiltInMetadata.document_name -> document.name
                                        BuiltInMetadata.last_update_date ->
                                            document.updateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                        BuiltInMetadata.upload_date ->
                                            document.createTime?.format(
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                                            )
                                        BuiltInMetadata.source -> "file_upload"
                                        BuiltInMetadata.uploader -> uploader?.account ?: ""
                                        else -> ""
                                    }
                            }
                        knowledgeBaseDocumentMetadataMapper.insert(relation)
                        log.info("Bound existing built in metadata ${builtInMetadata.name} to document ${document.id}")
                    }
                }
            }
        }

        return CommonRespDto.success()
    }

    override fun disableBuiltInMetadata(knowledgeBaseId: Int): CommonRespDto<Void> {
        // 获取知识库下的所有内置元数据
        val builtInMetadatas =
            knowledgeBaseMetadataMapper.selectList(
                KtQueryWrapper(KnowledgeBaseMetadataPo())
                    .eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId)
                    .eq(KnowledgeBaseMetadataPo::isBuiltIn, true),
            )

        // 删除所有内置元数据与文档的绑定关系
        builtInMetadatas.forEach { metadata ->
            knowledgeBaseDocumentMetadataMapper.delete(
                KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                    .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadata.id),
            )
            log.info("Removed binding relationships for built in metadata ${metadata.name} in knowledge base $knowledgeBaseId")
        }

        // 删除内置元数据
        knowledgeBaseMetadataMapper.delete(
            KtQueryWrapper(
                KnowledgeBaseMetadataPo(),
            ).eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeBaseMetadataPo::isBuiltIn, true),
        )

        return CommonRespDto.success()
    }

    override fun addMetadata(
        knowledgeBaseId: Int,
        name: String,
        type: MetadataType,
    ): CommonRespDto<Int> {
        // 验证元数据名称格式
        if (!validateMetadataName(name)) {
            return CommonRespDto.error("文档属性名称仅支持小写字母、数字和下划线字符，且必须以字母开头，不能超过50个字符")
        }

        val exists =
            knowledgeBaseMetadataMapper.exists(
                KtQueryWrapper(
                    KnowledgeBaseMetadataPo(),
                ).eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId).eq(KnowledgeBaseMetadataPo::name, name),
            )

        if (exists) {
            return CommonRespDto.error("文档属性已存在")
        }

        val po =
            KnowledgeBaseMetadataPo().apply {
                isBuiltIn = false
                this.knowledgeBaseId = knowledgeBaseId
                this.name = name
                this.type = type.name
                createTime = LocalDateTime.now()
                updateTime = LocalDateTime.now()
            }

        knowledgeBaseMetadataMapper.insert(po)
        return CommonRespDto.success(po.id)
    }

    override fun editMetadata(
        id: Int,
        name: String,
    ): CommonRespDto<Void> {
        // 验证元数据名称格式
        if (!validateMetadataName(name)) {
            return CommonRespDto.error("文档属性名称仅支持小写字母、数字和下划线字符，且必须以字母开头，不能超过50个字符")
        }

        val metadata = knowledgeBaseMetadataMapper.selectById(id) ?: return CommonRespDto.error("元数据不存在")

        // 检查名称是否已存在（排除当前记录）
        val exists =
            knowledgeBaseMetadataMapper.exists(
                KtQueryWrapper(KnowledgeBaseMetadataPo())
                    .eq(KnowledgeBaseMetadataPo::knowledgeBaseId, metadata.knowledgeBaseId)
                    .eq(KnowledgeBaseMetadataPo::name, name)
                    .ne(KnowledgeBaseMetadataPo::id, id),
            )

        if (exists) {
            return CommonRespDto.error("元数据名称已存在")
        }

        metadata.name = name
        metadata.updateTime = LocalDateTime.now()
        knowledgeBaseMetadataMapper.updateById(metadata)

        return CommonRespDto.success()
    }

    override fun deleteMetadata(id: Int): CommonRespDto<Void> {
        val metadata = knowledgeBaseMetadataMapper.selectById(id) ?: return CommonRespDto.error("元数据不存在")

        // 检查是否为内置元数据
        if (metadata.isBuiltIn == true) {
            return CommonRespDto.error("内置元数据不能删除")
        }

        // 删除元数据与文档的关系
        knowledgeBaseDocumentMetadataMapper.delete(
            KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                .eq(KnowledgeBaseDocumentMetadataPo::metadataId, id),
        )

        // 删除元数据
        knowledgeBaseMetadataMapper.deleteById(id)

        return CommonRespDto.success()
    }

    override fun getKnowledgeBaseMetadataList(knowledgeBaseId: Int): CommonRespDto<List<KnowledgeBaseMetadataDto>> {
        try {
            // 获取知识库下的所有元数据
            val metadatas =
                knowledgeBaseMetadataMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseMetadataPo())
                        .eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId)
                        .orderByDesc(KnowledgeBaseMetadataPo::createTime),
                )

            // 转换为DTO并统计不同值的数量
            val metadataDtos =
                metadatas.map { metadata ->
                    // 统计该元数据的不同值数量
                    val distinctValues =
                        knowledgeBaseDocumentMetadataMapper
                            .selectList(
                                KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                                    .select(KnowledgeBaseDocumentMetadataPo::value)
                                    .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadata.id)
                                    .isNotNull(KnowledgeBaseDocumentMetadataPo::value)
                                    .ne(KnowledgeBaseDocumentMetadataPo::value, ""),
                            ).mapNotNull { it.value }
                            .distinct()

                    KnowledgeBaseMetadataDto
                        .builder()
                        .id(metadata.id)
                        .name(metadata.name)
                        .type(MetadataType.valueOf(metadata.type ?: "String"))
                        .isBuiltIn(metadata.isBuiltIn)
                        .knowledgeBaseId(metadata.knowledgeBaseId)
                        .distinctValueCount(distinctValues.size)
                        .createTime(metadata.createTime)
                        .updateTime(metadata.updateTime)
                        .build()
                }

            log.info("成功获取知识库 $knowledgeBaseId 的元数据列表，共 ${metadataDtos.size} 个元数据")
            return CommonRespDto.success(metadataDtos)
        } catch (e: Exception) {
            log.error("获取知识库元数据列表失败", e)
            return CommonRespDto.error("获取知识库元数据列表失败：${e.message}")
        }
    }

    override fun getMetadataListByDocumentIds(documentIds: List<Int>): CommonRespDto<List<KnowledgeBaseMetadataWithValueDto>> {
        try {
            if (documentIds.isEmpty()) {
                return CommonRespDto.success(emptyList())
            }

            // 根据文档ID列表获取相关的元数据ID
            val metadataIds =
                knowledgeBaseDocumentMetadataMapper
                    .selectList(
                        KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                            .select(KnowledgeBaseDocumentMetadataPo::metadataId)
                            .`in`(KnowledgeBaseDocumentMetadataPo::documentId, documentIds)
                            .isNotNull(KnowledgeBaseDocumentMetadataPo::metadataId),
                    ).mapNotNull { it.metadataId }
                    .distinct()

            if (metadataIds.isEmpty()) {
                return CommonRespDto.success(emptyList())
            }

            // 根据元数据ID列表获取元数据详情
            val metadatas =
                knowledgeBaseMetadataMapper
                    .selectList(
                        KtQueryWrapper(KnowledgeBaseMetadataPo())
                            .`in`(KnowledgeBaseMetadataPo::id, metadataIds)
                            .orderByDesc(KnowledgeBaseMetadataPo::createTime),
                    )

            // 转换为DTO并获取元数据值
            val metadataDtos =
                metadatas.map { metadata ->
                    // 获取该元数据的所有值
                    val values =
                        knowledgeBaseDocumentMetadataMapper
                            .selectList(
                                KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                                    .select(KnowledgeBaseDocumentMetadataPo::value)
                                    .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadata.id)
                                    .`in`(KnowledgeBaseDocumentMetadataPo::documentId, documentIds)
                                    .isNotNull(KnowledgeBaseDocumentMetadataPo::value)
                                    .ne(KnowledgeBaseDocumentMetadataPo::value, ""),
                            ).mapNotNull { it.value }
                            .distinct()

                    // 统计该元数据的不同值数量
                    val distinctValues =
                        knowledgeBaseDocumentMetadataMapper
                            .selectList(
                                KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                                    .select(KnowledgeBaseDocumentMetadataPo::value)
                                    .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadata.id)
                                    .isNotNull(KnowledgeBaseDocumentMetadataPo::value)
                                    .ne(KnowledgeBaseDocumentMetadataPo::value, ""),
                            ).mapNotNull { it.value }
                            .distinct()

                    KnowledgeBaseMetadataWithValueDto
                        .builder()
                        .id(metadata.id)
                        .name(metadata.name)
                        .type(MetadataType.valueOf(metadata.type ?: "String"))
                        .isBuiltIn(metadata.isBuiltIn)
                        .knowledgeBaseId(metadata.knowledgeBaseId)
                        .distinctValueCount(distinctValues.size)
                        .createTime(metadata.createTime)
                        .updateTime(metadata.updateTime)
                        .values(values)
                        .build()
                }

            log.info("成功根据文档ID列表获取元数据列表，文档ID: $documentIds，共 ${metadataDtos.size} 个元数据")
            return CommonRespDto.success(metadataDtos)
        } catch (e: Exception) {
            log.error("根据文档ID列表获取元数据列表失败", e)
            return CommonRespDto.error("根据文档ID列表获取元数据列表失败：${e.message}")
        }
    }

    override fun editMetadataValueBatch(
        metadataAddRequests: List<KnowledgeBaseMetadataService.MetadataAddRequest>,
        metadataEditRequests: List<KnowledgeBaseMetadataService.MetadataEditRequest>,
        documentIds: List<Int>,
        applyToAllDocuments: Boolean,
        deletedMetadataIds: List<Int>?,
    ): CommonRespDto<Void> {
        try {
            // 验证文档是否存在
            val documents =
                knowledgeBaseDocumentMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .`in`(KnowledgeBaseDocumentPo::id, documentIds),
                )
            if (documents.size != documentIds.size) {
                return CommonRespDto.error("部分文档不存在")
            }

            // 删除绑定关系
            deletedMetadataIds.takeIf { !it.isNullOrEmpty() }?.also {
                val count =
                    knowledgeBaseDocumentMetadataMapper.delete(
                        KtQueryWrapper(
                            KnowledgeBaseDocumentMetadataPo(),
                        ).`in`(
                            KnowledgeBaseDocumentMetadataPo::documentId,
                            documentIds,
                        ).`in`(KnowledgeBaseDocumentMetadataPo::metadataId, deletedMetadataIds),
                    )
                log.info("移除${count}个元数据绑定，ID列表：$deletedMetadataIds")
            }

            // 处理编辑逻辑
            if (metadataEditRequests.isNotEmpty()) {
                val metadataIds = metadataEditRequests.map { it.metadataId }
                val metadatas =
                    knowledgeBaseMetadataMapper.selectList(
                        KtQueryWrapper(KnowledgeBaseMetadataPo())
                            .`in`(KnowledgeBaseMetadataPo::id, metadataIds),
                    )

                // 批量处理每个元数据的编辑
                metadataEditRequests.forEach { request ->
                    val metadataId = request.metadataId
                    val newValue = request.newValue

                    // 获取元数据信息以验证值类型
                    val metadata = metadatas.find { it.id == metadataId }
                    if (metadata != null) {
                        val metadataType = MetadataType.valueOf(metadata.type ?: "String")

                        // 验证元数据值是否符合类型要求
                        if (newValue != null && !validateMetadataValue(newValue, metadataType)) {
                            val errorMessage =
                                when (metadataType) {
                                    MetadataType.Number -> "元数据值必须是数字格式"
                                    MetadataType.Time -> "元数据值必须是时间格式（YYYY-MM-dd HH:mm）"
                                    MetadataType.String -> "元数据值格式不正确"
                                }
                            log.error("元数据值验证失败，元数据ID: $metadataId，值: $newValue，类型: $metadataType")
                            return CommonRespDto.error(errorMessage)
                        }
                    }

                    // 获取当前文档的元数据值记录
                    val existingMetadataValues =
                        knowledgeBaseDocumentMetadataMapper.selectList(
                            KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                                .eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadataId)
                                .`in`(KnowledgeBaseDocumentMetadataPo::documentId, documentIds),
                        )

                    if (applyToAllDocuments || documentIds.size == 1) {
                        // 应用于所有选定文档
                        val documentsWithMetadata = existingMetadataValues.map { it.documentId }.toSet()
                        val documentsWithoutMetadata = documentIds.filter { it !in documentsWithMetadata }

                        // 更新已有元数据值的文档
                        if (existingMetadataValues.isNotEmpty()) {
                            existingMetadataValues.forEach { metadataValue ->
                                metadataValue.value = newValue
                                metadataValue.updateTime = LocalDateTime.now()
                                knowledgeBaseDocumentMetadataMapper.updateById(metadataValue)
                            }
                        }

                        // 为没有元数据值的文档创建新的元数据值记录
                        documentsWithoutMetadata.forEach { documentId ->
                            val newMetadataValue =
                                KnowledgeBaseDocumentMetadataPo().apply {
                                    this.documentId = documentId
                                    this.metadataId = metadataId
                                    this.value = newValue
                                    this.name = metadata?.name
                                    createTime = LocalDateTime.now()
                                    updateTime = LocalDateTime.now()
                                }
                            knowledgeBaseDocumentMetadataMapper.insert(newMetadataValue)
                        }

                        log.info("成功为所有选定文档编辑元数据值，元数据ID: $metadataId，新值: $newValue，文档ID: $documentIds")
                    } else {
                        // 仅对具有元数据的文档应用编辑
                        if (existingMetadataValues.isEmpty()) {
                            log.warn("选定的文档中没有元数据ID $metadataId，跳过编辑")
                            return@forEach
                        }

                        // 将所有不同的值统一修改为相同值
                        existingMetadataValues.forEach { metadataValue ->
                            metadataValue.value = newValue
                            metadataValue.updateTime = LocalDateTime.now()
                            knowledgeBaseDocumentMetadataMapper.updateById(metadataValue)
                        }

                        log.info("成功为具有元数据的文档编辑元数据值，元数据ID: $metadataId，新值: $newValue，文档ID: $documentIds")
                    }
                }

                log.info("成功批量编辑元数据值，共处理 ${metadataEditRequests.size} 个元数据，文档ID: $documentIds")
            }

            metadataAddRequests.takeIf { it.isNotEmpty() }?.also { addReqs ->
                val metadataIds = metadataAddRequests.map { it.metadataId }
                val metadatas =
                    knowledgeBaseMetadataMapper.selectList(
                        KtQueryWrapper(KnowledgeBaseMetadataPo())
                            .`in`(KnowledgeBaseMetadataPo::id, metadataIds),
                    )

                // 为没有元数据值的文档创建新的元数据值记录
                addReqs.forEach { request ->
                    val metadataId = request.metadataId
                    val newValue = request.newValue

                    // 获取元数据信息以验证值类型
                    val metadata = metadatas.find { it.id == metadataId }
                    if (metadata != null) {
                        val metadataType = MetadataType.valueOf(metadata.type ?: "String")

                        // 验证元数据值是否符合类型要求
                        if (newValue != null && !validateMetadataValue(newValue, metadataType)) {
                            val errorMessage =
                                when (metadataType) {
                                    MetadataType.Number -> "元数据值必须是数字格式"
                                    MetadataType.Time -> "元数据值必须是时间格式（YYYY-MM-dd HH:mm）"
                                    MetadataType.String -> "元数据值格式不正确"
                                }
                            log.error("元数据值验证失败，元数据ID: $metadataId，值: $newValue，类型: $metadataType")
                            return CommonRespDto.error(errorMessage)
                        }
                    }

                    documentIds.forEach { documentId ->
                        val exists =
                            knowledgeBaseDocumentMetadataMapper.selectOne(
                                KtQueryWrapper(
                                    KnowledgeBaseDocumentMetadataPo(),
                                ).eq(
                                    KnowledgeBaseDocumentMetadataPo::documentId,
                                    documentId,
                                ).eq(KnowledgeBaseDocumentMetadataPo::metadataId, metadataId),
                            )

                        if (exists == null) {
                            val newMetadataValue =
                                KnowledgeBaseDocumentMetadataPo().apply {
                                    this.documentId = documentId
                                    this.metadataId = metadataId
                                    this.value = newValue
                                    this.name = metadata?.name
                                    createTime = LocalDateTime.now()
                                    updateTime = LocalDateTime.now()
                                }
                            knowledgeBaseDocumentMetadataMapper.insert(newMetadataValue)
                        } else {
                            exists.value = newValue
                            knowledgeBaseDocumentMetadataMapper.updateById(exists)
                        }
                    }
                }
            }

            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("批量编辑元数据值失败", e)
            return CommonRespDto.error("批量编辑元数据值失败：${e.message}")
        }
    }

    override fun getSameMetadataByKnowledgeBaseIds(knowledgeBaseIds: List<Int>): CommonRespDto<List<KnowledgeBaseMetadataBaseDto>> {
        if (knowledgeBaseIds.isEmpty()) {
            return CommonRespDto.success(emptyList())
        }

        val metadataList =
            knowledgeBaseMetadataMapper.selectList(
                KtQueryWrapper(KnowledgeBaseMetadataPo()).`in`(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseIds),
            )

        if (knowledgeBaseIds.size == 1) {
            val result =
                metadataList
                    .map {
                        KnowledgeBaseMetadataBaseDto(
                            it.name,
                            MetadataType.valueOf(it.type ?: MetadataType.String.name),
                        )
                    }.toList()
            return CommonRespDto.success(result)
        }

        // 按照知识库ID分组
        val grouped = metadataList.groupBy { it.knowledgeBaseId }

        // 每个知识库的元数据键集合
        val sets =
            grouped.map { (_, list) ->
                list
                    .map {
                        KnowledgeBaseMetadataBaseDto(
                            it.name,
                            MetadataType.valueOf(it.type ?: MetadataType.String.name),
                        )
                    }.toSet()
            }

        // 找到共有部分
        val commonKeys = sets.reduceOrNull { acc, set -> acc.intersect(set) } ?: emptySet()

        return CommonRespDto.success(commonKeys.toList())
    }

    override fun updateLastUpdateDate(
        knowledgeBaseId: Int,
        documentId: Int,
    ): CommonRespDto<Void> {
        val metadata =
            knowledgeBaseMetadataMapper.selectOne(
                KtQueryWrapper(
                    KnowledgeBaseMetadataPo(),
                ).eq(
                    KnowledgeBaseMetadataPo::isBuiltIn,
                    true,
                ).eq(
                    KnowledgeBaseMetadataPo::name,
                    BuiltInMetadata.last_update_date.name,
                ).eq(KnowledgeBaseMetadataPo::knowledgeBaseId, knowledgeBaseId),
            )
        metadata?.also {
            knowledgeBaseDocumentMetadataMapper.update(
                KtUpdateWrapper(
                    KnowledgeBaseDocumentMetadataPo(),
                ).eq(
                    KnowledgeBaseDocumentMetadataPo::documentId,
                    documentId,
                ).eq(
                    KnowledgeBaseDocumentMetadataPo::metadataId,
                    metadata.id,
                ).set(KnowledgeBaseDocumentMetadataPo::value, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
            )
        }
        return CommonRespDto.success()
    }
}
