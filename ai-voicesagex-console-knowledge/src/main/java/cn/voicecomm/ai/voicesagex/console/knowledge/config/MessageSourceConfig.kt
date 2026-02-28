package cn.voicecomm.ai.voicesagex.console.knowledge.config

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.SpringContUtil
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource

/**
 * @description
 *
 * @author ryc
 * @date 2025/9/2 18:28
 */
@Configuration
class MessageSourceConfig {
    @Bean
    fun messageSource(): ReloadableResourceBundleMessageSource {
        return ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
            setFallbackToSystemLocale(true)
        }
    }

    @Bean
    fun springContUtil(applicationContext: ApplicationContext): SpringContUtil {
        return SpringContUtil().apply {
            setApplicationContext(applicationContext)
        }
    }
}