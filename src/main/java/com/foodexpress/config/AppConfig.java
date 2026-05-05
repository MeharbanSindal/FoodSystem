package com.foodexpress.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableAsync
@ComponentScan(basePackages = {
    "com.foodexpress.service",
    "com.foodexpress.dao"
})
public class AppConfig {

	@Bean
	public JavaMailSender javaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);

	    mailSender.setUsername("meharbansindal@gmail.com");
	    mailSender.setPassword("boil owkj ugsb ghjl");

	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");

	    return mailSender;
	}
}