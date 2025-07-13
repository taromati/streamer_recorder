package me.taromati.streamerrecorder.web.config;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setDetectAllHandlerAdapters(true);
        dispatcherServlet.setDetectAllHandlerExceptionResolvers(true);
        dispatcherServlet.setDetectAllHandlerMappings(true);
        dispatcherServlet.setDetectAllViewResolvers(true);
//        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        dispatcherServlet.setDispatchOptionsRequest(true);
        dispatcherServlet.setEnableLoggingRequestDetails(true);
        return dispatcherServlet;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        List<String> originList = Lists.newArrayList();
        originList.add("http://localhost:6060");

        String[] origins = originList.toArray(new String[0]);

        registry.addMapping("/**")
//                .allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .maxAge(3600);
    }
}
