package cn.voicecomm.ai.voicesagex.console.knowledge.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class RetrievalTestRespVo(
    val code: Int?,
    val msg: String?,
    val done: Boolean?,
    val data: RetrievalTestRespDataVo?,
    val usage: RetrievalTestRespUsageVo?,
)

data class RetrievalTestRespDataVo(
    val result: List<RetrievalTestRespDataResultVo>?,
)

data class RetrievalTestRespDataResultVo(
    val content: String?,
    val title: String?,
    val url: String?,
    val icon: String?,
    val metadata: RetrievalTestRespDataResultMetadataVo?,
)

data class RetrievalTestRespDataResultMetadataVo(
    val score: Float?,
    @field:JsonProperty("content_len")
    val contentLen: Int?,
    val idx: Int?,
)

data class RetrievalTestRespUsageVo(
    @field:JsonProperty("prompt_tokens") val promptTokens: Int,
    @field:JsonProperty("completion_tokens")
    val completionTokens: Int,
    @field:JsonProperty("total_tokens")
    val totalTokens: Int,
)
