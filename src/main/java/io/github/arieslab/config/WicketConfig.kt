package io.github.arieslab.config

import io.github.arieslab.WicketApplication
import jakarta.servlet.DispatcherType
import org.apache.wicket.protocol.http.WicketFilter
import org.apache.wicket.spring.SpringWebApplicationFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WicketConfig {

    @Bean
    open fun wicketFilter(): FilterRegistrationBean<WicketFilter> {
        val filter = WicketFilter()
        val registration = FilterRegistrationBean<WicketFilter>()
        registration.setFilter(filter)
        registration.setInitParameters(mapOf(
            "applicationClassName" to WicketApplication::class.java.name,
            "applicationFactoryClassName" to SpringWebApplicationFactory::class.java.name,
            "filterMappingUrlPattern" to "/*"
        ))
        registration.addUrlPatterns("/*")
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR)
        registration.order = 1
        return registration
    }
}
