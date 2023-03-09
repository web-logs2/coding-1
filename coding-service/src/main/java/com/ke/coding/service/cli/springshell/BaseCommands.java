//package com.ke.coding.service.cli.springshell;
//
//import static com.ke.coding.api.enums.Constants.ROOT_PATH;
//
//import com.google.common.base.Joiner;
//import com.ke.coding.api.dto.cli.Command;
//import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
//import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
//import com.ke.coding.service.filesystem.fatservice.FileSystem;
//import com.ke.risk.safety.common.util.json.JsonUtils;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.shell.standard.ShellComponent;
//import org.springframework.shell.standard.ShellMethod;
//import org.springframework.shell.standard.ShellOption;
//
///**
// * @author: xueyunlong001@ke.com
// * @time: 2023/3/6 18:18
// * @description:
// */
//@ShellComponent()
//public class BaseCommands {
//
//
//	@Autowired
//	FileSystem fileSystem;
//
//	private  String currentPath = ROOT_PATH;
//
//	@ShellMethod("展示当前目录条目")
//	public String ll() {
//		FileSystemActionResult result = fileSystem.execute(Command.build("ll", currentPath));
//		List<DirectoryEntrySubInfo> directoryEntrySubInfos = JsonUtils.parseStr2List(result.getData(), DirectoryEntrySubInfo.class);
//		List<String> lines = new ArrayList<>();
//		for (DirectoryEntrySubInfo directoryEntrySubInfo : directoryEntrySubInfos) {
//			String sb = StringUtils.rightPad(directoryEntrySubInfo.getFileSize() + "", 10, " ")
//				+ StringUtils.rightPad(directoryEntrySubInfo.getAccessDate() + "", 14, " ")
//				+ directoryEntrySubInfo.getFileName();
//			lines.add(sb);
//		}
//		return Joiner.on("\n").join(lines);
//	}
//
//	@ShellMethod("format")
//	public String format() {
//		FileSystemActionResult result = fileSystem.execute(Command.build("format", currentPath));
//		cd(new String[]{ROOT_PATH});
//		return result.getData();
//	}
//
//	@ShellMethod("mkdir")
//	public String mkdir(@ShellOption(arity = 1) String[] params) {
//		FileSystemActionResult result = fileSystem.execute(Command.build("mkdir", currentPath, Arrays.asList(params)));
//		return result.getData();
//	}
//
//	@ShellMethod("touch")
//	public String touch(@ShellOption(arity = 1) String[] params) {
//		FileSystemActionResult result = fileSystem.execute(Command.build("touch", currentPath, Arrays.asList(params)));
//		return result.getData();
//	}
//
//	@ShellMethod("cd")
//	public String cd(@ShellOption(arity = 1) String[] params) {
//		FileSystemActionResult result = fileSystem.execute(Command.build("cd", currentPath, Arrays.asList(params)));
//		if (result.isSuccess()){
//			currentPath = result.getData();
//			return result.getData();
//		}
//		return result.getMessage();
//
//	}
//
//	@ShellMethod("pwd")
//	public String pwd() {
//		return currentPath;
//	}
//
//	@ShellMethod("echo")
//	public String echo(@ShellOption(arity = 3) String[] params) {
//		FileSystemActionResult result = fileSystem.execute(Command.build("echo", currentPath, Arrays.asList(params)));
//		return result.getData();
//	}
//
//	@ShellMethod("cat")
//	public String cat(@ShellOption(arity = 1) String[] params) {
//		FileSystemActionResult result = fileSystem.execute(Command.build("cat", currentPath, Arrays.asList(params)));
//		return result.getData();
//	}
//}
