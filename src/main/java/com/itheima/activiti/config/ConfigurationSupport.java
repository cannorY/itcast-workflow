package com.itheima.activiti.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.itheima.activiti.auth.JwtAuthenticationTokenFilter;
import com.itheima.activiti.common.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * 工具类：
 * 1、Swagger的配置类
 * 2、Json转换工具
 * 3、feign时间序列化工具
 */
@EnableSwagger2
@EnableKnife4j
@Configuration
@Slf4j
public class ConfigurationSupport extends WebMvcConfigurationSupport {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itheima.activiti.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Activiti7")
                .version("1.0")
                .description("Activiti7接口文档")
                .build();
    }

    /**
     * 防止@EnableMvc把默认的静态资源路径覆盖了，手动设置的方式
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 增加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //默认拦截所有路径
        registry.addInterceptor(SpringContextHolder.getBean(JwtAuthenticationTokenFilter.class))
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources")
                .excludePathPatterns("/error")
                .excludePathPatterns("/v2/api-docs")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**");
    }

    /**
     * 解决序列化null问题
     * 默认转成 ''
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("初始化jackson配置");
        //添加json转换
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(jackson2HttpMessageConverter);
        //追加默认转换器
        super.addDefaultHttpMessageConverters(converters);
    }

    class JacksonObjectMapper extends ObjectMapper {
        public JacksonObjectMapper() {
            super();
            //收到未知属性时不报异常
            this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

            //反序列化时，属性不存在的兼容处理
            this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            SimpleModule simpleModule = new SimpleModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                    .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                    .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))
                    .addSerializer(BigInteger.class, ToStringSerializer.instance)
                    .addSerializer(Long.class, ToStringSerializer.instance)
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                    .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                    .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))
                    .addSerializer(Date.class, new CustomerDateSerializer());

            this.registerModule(simpleModule);
        }
    }


    /**
     * 解决feign时间序列化问题
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return (builder) -> {
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .serializerByType(Date.class, new CustomerDateSerializer())
                    .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        };
    }

    class CustomerDateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(date));
        }
    }

}
