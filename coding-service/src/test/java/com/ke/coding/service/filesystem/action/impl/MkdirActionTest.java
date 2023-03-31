package com.ke.coding.service.filesystem.action.impl;


import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.service.disk.FileDisk;
import com.ke.coding.service.filesystem.action.ActionDispatcher;
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
@Import({MkdirAction.class, ActionDispatcher.class, FileDisk.class,  DataRegionService.class, DataClusterService.class,
	FatRegionService.class, RootDirectoryRegionService.class})
public class MkdirActionTest {

	@Autowired
	MkdirAction mkdirAction;

	@Autowired
	ActionDispatcher fat16xFileSystemService;

	@Test
	public void mkdir(){
		Command command = new Command();
		command.setParams(Collections.singletonList("1"));
		command.setCurrentPath("/");
		FileSystemResult run = mkdirAction.run(command);
		System.out.println(run.getData());

		command.setParams(Collections.singletonList("11"));
		command.setCurrentPath("/1");
		run = mkdirAction.run(command);
		System.out.println(run.getData());
	}

}
