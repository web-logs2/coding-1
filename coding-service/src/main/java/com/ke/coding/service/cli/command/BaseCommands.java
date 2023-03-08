package com.ke.coding.service.cli.command;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.service.filesystem.FileSystem;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
@ShellComponent()
public class BaseCommands {


	@Autowired
	FileSystem fileSystem;

	private  String currentPath = ROOT_PATH;

	@ShellMethod("展示当前目录条目")
	public String ls() {
		FileSystemActionResult result = fileSystem.execute(Command.build("ls", currentPath));
		return result.getData();
	}

	@ShellMethod("format")
	public String format() {
		return "test";
	}

	@ShellMethod("mkdir")
	public String mkdir(@ShellOption(arity = 1) String[] params) {
		FileSystemActionResult result = fileSystem.execute(Command.build("mkdir", currentPath, Arrays.asList(params)));
		return result.getData();
	}

	@ShellMethod("touch")
	public String touch(@ShellOption(arity = 1) String[] params) {
		FileSystemActionResult result = fileSystem.execute(Command.build("touch", currentPath, Arrays.asList(params)));
		return result.getData();
	}

	@ShellMethod("cd")
	public String cd(@ShellOption(arity = 1) String[] params) {
		FileSystemActionResult result = fileSystem.execute(Command.build("cd", currentPath, Arrays.asList(params)));
		if (result.isSuccess()){
			currentPath = result.getData();
			return result.getData();
		}
		return result.getMessage();

	}

	@ShellMethod("pwd")
	public String pwd() {
		return currentPath;
	}

	@ShellMethod("echo")
	public String echo(@ShellOption(arity = 3) String[] params) {
		FileSystemActionResult result = fileSystem.execute(Command.build("echo", currentPath, Arrays.asList(params)));
		return result.getData();
	}

	@ShellMethod("cat")
	public String cat(@ShellOption(arity = 1) String[] params) {
		FileSystemActionResult result = fileSystem.execute(Command.build("cat", currentPath, Arrays.asList(params)));
		return result.getData();
	}
}
