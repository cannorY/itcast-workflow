package com.itheima.activiti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(value = "com.itheima")
@EnableFeignClients("com.itheima")
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 运行成功! 访问连接:\n\t" +
                        "Knife4j: \t\thttp://{}:{}/doc.html\n\t" +
                        "Version: \t\t{}\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                "127.0.0.1",
                env.getProperty("server.port"),
                "2021-003");
    }
}
