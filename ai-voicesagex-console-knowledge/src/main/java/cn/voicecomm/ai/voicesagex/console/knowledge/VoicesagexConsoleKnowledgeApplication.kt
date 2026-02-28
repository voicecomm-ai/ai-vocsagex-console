package cn.voicecomm.ai.voicesagex.console.knowledge

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication(scanBasePackages = ["org.nebula.contrib", "cn.voicecomm.ai.voicesagex.console.knowledge"])
@EnableDubbo
@MapperScan("cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper")
class Application

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
    runApplication<Application>(*args)
}


