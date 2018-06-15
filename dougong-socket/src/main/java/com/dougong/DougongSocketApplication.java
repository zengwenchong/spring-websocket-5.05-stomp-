package com.dougong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootConfiguration
@Configuration  
@ComponentScan
@EnableAutoConfiguration
@ServletComponentScan
public class DougongSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(DougongSocketApplication.class, args);
		/*
         * Banner.Mode.OFF:关闭;
         * Banner.Mode.CONSOLE:控制台输出，默认方式;
         * Banner.Mode.LOG:日志输出方式;
         */
		//application.setBannerMode(Banner.Mode.OFF);
	}
}
