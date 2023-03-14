package com.ke.coding.service.filesystem.action.impl;


import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.service.disk.FileDisk;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xFileSystemService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.DataClusterService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.DataRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.FatRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.RootDirectoryRegionService;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@Import({MkdirAction.class, Fat16xFileSystemService.class, FileDisk.class,  DataRegionService.class, DataClusterService.class,
	FatRegionService.class, RootDirectoryRegionService.class})
public class MkdirActionTest {

	@Autowired
	MkdirAction mkdirAction;

	@Autowired
	Fat16xFileSystemService fat16xFileSystemService;

	@Test
	public void mkdir(){
		Command command = new Command();
		command.setParams(Collections.singletonList("1"));
		command.setCurrentPath("/");
		FileSystemActionResult run = mkdirAction.run(command, fat16xFileSystemService.getFat16xFileSystem());
		System.out.println(run.getData());

		command.setParams(Collections.singletonList("11"));
		command.setCurrentPath("/1");
		run = mkdirAction.run(command, fat16xFileSystemService.getFat16xFileSystem());
		System.out.println(run.getData());
	}

}
