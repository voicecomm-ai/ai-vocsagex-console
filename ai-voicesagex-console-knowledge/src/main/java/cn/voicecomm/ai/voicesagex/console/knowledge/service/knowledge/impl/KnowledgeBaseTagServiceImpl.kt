package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseTagService
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseTagDto
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseMapper
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseTagMapper
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeBaseTagRelationMapper
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBasePo
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseTagPo
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseTagRelationPo
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.dubbo.config.annotation.DubboService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@DubboService
@Slf4j
class KnowledgeBaseTagServiceImpl(
    private val knowledgeBaseTagMapper: KnowledgeBaseTagMapper,
    private val knowledgeBaseTagRelationMapper: KnowledgeBaseTagRelationMapper,
    private val knowledgeBaseMapper: KnowledgeBaseMapper,
) : KnowledgeBaseTagService {
    override fun createTag(name: String): CommonRespDto<Int> =
        if (knowledgeBaseTagMapper.selectCount(Wrappers.emptyWrapper()) >= 30) {
            CommonRespDto.error("标签数量达到最大值")
        } else if (knowledgeBaseTagMapper.exists(
                KtQueryWrapper(KnowledgeBaseTagPo()).eq(
                    KnowledgeBaseTagPo::name,
                    name,
                ),
            )
        ) {
            CommonRespDto.error("标签已存在")
        } else {
            KnowledgeBaseTagPo()
                .apply {
                    this.name = name
                    createBy = UserAuthUtil.getUserId()
                    updateBy = UserAuthUtil.getUserId()
                    createTime = LocalDateTime.now()
                    updateTime = LocalDateTime.now()
                }.also {
                    knowledgeBaseTagMapper.insert(
                        it,
                    )
                }.let {
                    CommonRespDto.success(it.id)
                }
        }

    override fun isDeletable(id: Int?): Boolean =
        !knowledgeBaseTagRelationMapper.exists(
            KtQueryWrapper(KnowledgeBaseTagRelationPo()).eq(
                KnowledgeBaseTagRelationPo::tagId,
                id,
            ),
        )

    override fun deleteTag(id: Int): CommonRespDto<Void> {
        knowledgeBaseTagRelationMapper.delete(
            KtQueryWrapper(KnowledgeBaseTagRelationPo())
                .eq(KnowledgeBaseTagRelationPo::tagId, id),
        )
        knowledgeBaseTagMapper.deleteById(id)

        return CommonRespDto.success()
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun bindTag(
        tagIds: List<Int>?,
        knowledgeBaseId: Int,
    ): CommonRespDto<Void> {
        // 检查知识库是否存在
        if (!knowledgeBaseMapper.exists(
                KtQueryWrapper(KnowledgeBasePo())
                    .eq(KnowledgeBasePo::id, knowledgeBaseId),
            )
        ) {
            return CommonRespDto.error("知识库不存在")
        }

        // 获取已存在的标签ID
        val existsTagIds =
            knowledgeBaseTagMapper
                .selectObjs<Int>(
                    KtQueryWrapper(KnowledgeBaseTagPo())
                        .select(KnowledgeBaseTagPo::id),
                )

        // 删除所有绑定关系
        knowledgeBaseTagRelationMapper.delete(
            KtQueryWrapper(KnowledgeBaseTagRelationPo()).eq(
                KnowledgeBaseTagRelationPo::knowledgeBaseId,
                knowledgeBaseId,
            ),
        )

        // 处理标签绑定
        tagIds.takeIf { !it.isNullOrEmpty() }?.filter { it in existsTagIds }?.forEach { tagId ->
            // 创建绑定关系
            knowledgeBaseTagRelationMapper.insert(
                KnowledgeBaseTagRelationPo().apply {
                    this.tagId = tagId
                    this.knowledgeBaseId = knowledgeBaseId
                },
            )
        }

        return CommonRespDto.success()
    }

    override fun editTag(
        id: Int,
        name: String,
    ): CommonRespDto<Void> =
        knowledgeBaseTagMapper.selectById(id)?.let { tag ->
            val exists =
                knowledgeBaseTagMapper.exists(
                    KtQueryWrapper(KnowledgeBaseTagPo())
                        .eq(
                            KnowledgeBaseTagPo::name,
                            name,
                        ).ne(KnowledgeBaseTagPo::id, id),
                )

            if (exists) {
                return CommonRespDto.error("标签名称已存在")
            }

            tag.apply {
                this.name = name
                updateTime = LocalDateTime.now()
            }
            knowledgeBaseTagMapper.updateById(tag)
            CommonRespDto.success()
        } ?: CommonRespDto.error("标签不存在")

    override fun list(name: String?): CommonRespDto<List<KnowledgeBaseTagDto>> =
        knowledgeBaseTagMapper
            .selectList(
                KtQueryWrapper(KnowledgeBaseTagPo())
                    .apply(StringUtils.isNotEmpty(name), "name ILIKE {0}", "%${SpecialCharUtil.replaceSpecialWord(name)}%")
                    .orderByDesc(KnowledgeBaseTagPo::createTime),
            ).let { tags ->
                if (tags.isEmpty()) {
                    CommonRespDto.success(emptyList())
                } else {
                    val tagBaseNumMap =
                        knowledgeBaseTagRelationMapper
                            .selectList(
                                KtQueryWrapper(KnowledgeBaseTagRelationPo()).`in`(
                                    KnowledgeBaseTagRelationPo::tagId,
                                    tags.map { it.id },
                                ),
                            ).groupingBy { it.tagId }
                            .eachCount()
                    CommonRespDto.success(
                        tags.map { tag ->
                            KnowledgeBaseTagDto
                                .builder()
                                .name(tag.name)
                                .id(tag.id)
                                .knowledgeBaseNum(tagBaseNumMap.getOrDefault(tag.id, 0))
                                .build()
                        },
                    )
                }
            }
}
