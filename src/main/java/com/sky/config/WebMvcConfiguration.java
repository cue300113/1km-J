package com.sky.config;

import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {


    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/register")
        .excludePathPatterns("/api/login");
    }

    /**
     * 通过knife4j生成接口文档
     * @return
     */





    //扩展springmvc消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展springmvc消息转换器...");
        //创建一个消息转换器对象
        MappingJackson2HttpMessageConverter mappingJackson2MessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,mappingJackson2MessageConverter);
    }
}
