package io.github.arieslab.config;

import io.github.arieslab.WicketApplication;
import jakarta.servlet.DispatcherType;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class WicketConfig {

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilter() {
        var filter = new WicketFilter();
        var registration = new FilterRegistrationBean<WicketFilter>();
        registration.setFilter(filter);
        registration.setInitParameters(Map.of(
            "applicationClassName", WicketApplication.class.getName(),
            "applicationFactoryClassName", SpringWebApplicationFactory.class.getName(),
            "filterMappingUrlPattern", "/*"
        ));
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        registration.setOrder(1);
        return registration;
    }
}
