package com.ke.coding;

import com.ke.coding.service.cli.stdincli.InputResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务启动类
 *
 * @author keboot
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	InputResolver resolver;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Override
	public void run(String... args) {
		resolver.run();
	}
}
