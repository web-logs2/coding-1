package com.ke.coding.service.filesystem.fat16xservice;


import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xFileSystemService;
import java.util.Arrays;
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
		/*
		/test
		/test/111
		/test/222
		/test/222/xyl
		/test/222/xyl1
		 */
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
		/*
		/test
		/test/111
		/test/222
		/test/222/xyl
		/test/222/xyl1
		/test/222/333.jpg
		 */
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

	@Test
	public void echo(){
		/*
		/xyl.dat
		/test
		/test/xyl1.dat
		/test/111
		/test/111/xyl2.dat
		/test/222
		/test/222/xyl
		/test/222/xyl1
		/test/222/333.jpg
		 */
		touch();
		Command command = new Command();
		command.setCurrentPath("/");
		command.setParams(Arrays.asList("this is data", "xyl.dat"));
		//首次写入/xyl.dat
		fat16xFileSystemService.echo(command);

		command.setCurrentPath("/");
		command.setParams(Arrays.asList("this is data1", "xyl.dat"));
		//追加写入/xyl.dat
		fat16xFileSystemService.echo(command);

		command.setCurrentPath("/test");
		command.setParams(Arrays.asList("this is data1", "xyl1.dat"));
		//首次写入/test/xyl1.dat
		fat16xFileSystemService.echo(command);

		command.setCurrentPath("/test/111");
		command.setParams(Arrays.asList("this is data2", "xyl2.dat"));
		//首次写入/test/xyl1.dat
		fat16xFileSystemService.echo(command);
	}

	@Test
	public void cat(){
		/*
		/xyl.dat
		/test
		/test/xyl1.dat
		/test/111
		/test/111/xyl2.dat
		/test/222
		/test/222/xyl
		/test/222/xyl1
		/test/222/333.jpg
		 */
		touch();
		Command command = new Command();
		command.setCurrentPath("/");
		command.setParams(Arrays.asList("this is data", "xyl.dat"));
		//首次写入/xyl.dat
		fat16xFileSystemService.echo(command);

		command.setCurrentPath("/");
		command.setParams(Collections.singletonList("xyl.dat"));
		//首次写入/xyl.dat
		FileSystemActionResult cat = fat16xFileSystemService.cat(command);
		Assert.assertEquals("this is data", cat.getData());

		command.setParams(Arrays.asList("this is data1", "xyl.dat"));
		//追加写入/xyl.dat
		fat16xFileSystemService.echo(command);
		command.setParams(Collections.singletonList("xyl.dat"));
		cat = fat16xFileSystemService.cat(command);
		Assert.assertEquals("this is datathis is data1", cat.getData());

		command.setCurrentPath("/test");
		command.setParams(Arrays.asList("test first", "xyl1.dat"));
		//首次写入/test/xyl1.dat
		fat16xFileSystemService.echo(command);
		command.setParams(Collections.singletonList("xyl1.dat"));
		cat = fat16xFileSystemService.cat(command);
		Assert.assertEquals("test first", cat.getData());

		command.setCurrentPath("/test/111");
		command.setParams(Arrays.asList("test second", "xyl2.dat"));
		fat16xFileSystemService.echo(command);
		command.setParams(Collections.singletonList("xyl2.dat"));
		cat = fat16xFileSystemService.cat(command);
		Assert.assertEquals("test second", cat.getData());

		command.setParams(Arrays.asList("test second", "xyl2.dat"));
		fat16xFileSystemService.echo(command);
		command.setParams(Collections.singletonList("xyl2.dat"));
		cat = fat16xFileSystemService.cat(command);
		Assert.assertEquals("test secondtest second", cat.getData());
	}
}
