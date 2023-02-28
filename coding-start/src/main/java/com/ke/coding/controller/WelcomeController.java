package com.ke.coding.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例控制器
 * @author keboot
 */
@RestController
public class WelcomeController {
	@GetMapping("/api/v1/hello")
	public String hello() {
		return "hello";
	}
}
