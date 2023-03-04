package com.ke.coding.service.filesystem.impl;


import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@Import(Fat16xFileSystemService.class)
public class Fat16xFileSystemServiceTest {

	@Autowired
	Fat16xFileSystemService fat16xFileSystemService;

	@Test
	public void mkdir() {
		Command command = new Command();
		command.setParams(Collections.singletonList("test"));
		command.setCurrentPath("/");
		fat16xFileSystemService.mkdir(command);
		command.setParams(Collections.singletonList("111"));
		command.setCurrentPath("/test");
		fat16xFileSystemService.mkdir(command);
		command.setParams(Collections.singletonList("222"));
		command.setCurrentPath("/test");
		fat16xFileSystemService.mkdir(command);
		command.setParams(Collections.singletonList("xyl"));
		command.setCurrentPath("/test/222");
		fat16xFileSystemService.mkdir(command);
		command.setParams(Collections.singletonList("xyl1"));
		command.setCurrentPath("/test/222");
		fat16xFileSystemService.mkdir(command);
	}

	@Test
	public void touch(){
		mkdir();
		Command command = new Command();
		command.setParams(Collections.singletonList("333.jpg"));
		command.setCurrentPath("/test/222");
		fat16xFileSystemService.touch(command);
	}

	@Test
	public void ls(){
		touch();
		Command command = new Command();
		command.setCurrentPath("/");
		FileSystemActionResult ls = fat16xFileSystemService.ls(command);
		Assert.assertEquals("[\"/test\"]", ls.getData());

		command.setCurrentPath("/test");
		ls = fat16xFileSystemService.ls(command);
		Assert.assertEquals("[\"/111\",\"/222\"]", ls.getData());

		command.setCurrentPath("/test/222");
		ls = fat16xFileSystemService.ls(command);
		Assert.assertEquals("[\"/xyl\",\"/xyl1\",\"333.jpg\"]", ls.getData());
	}
}
