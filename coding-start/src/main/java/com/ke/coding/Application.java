package com.ke.coding;

import com.ke.coding.service.cli.stdincli.LocalInputResolver;
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

	LocalInputResolver resolver = new LocalInputResolver();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Override
	public void run(String... args) {
		resolver.run();
	}
}
