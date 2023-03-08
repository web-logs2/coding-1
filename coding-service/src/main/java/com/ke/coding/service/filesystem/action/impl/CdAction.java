package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.service.filesystem.action.AbstractAction;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class CdAction extends AbstractAction {

	@Autowired
	LsAction lsAction;

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		String currentPath = command.getCurrentPath();
		String cdPath = command.getParams().get(0);
		if (cdPath.equals("..")) {
			if (currentPath.equals(ROOT_PATH)) {
				return FileSystemActionResult.success(ROOT_PATH);
			} else {
				String result = "";
				String[] split = command.getCurrentPath().split(PATH_SPLIT);
				for (int i = 0; i < split.length - 1; i++) {
					result = ROOT_PATH + split[i];
				}
				return FileSystemActionResult.success(result);
			}
		}else if (cdPath.equals(ROOT_PATH)){
			return FileSystemActionResult.success(ROOT_PATH);
		}else {
			FileSystemActionResult result = lsAction.run(Command.build(currentPath), fat16xFileSystem);
			String data = result.getData();
			List<String> strings = JsonUtils.parseStr2List(data, String.class);
			cdPath = cdPath.contains("/") ? cdPath : ROOT_PATH + cdPath;
			if (!strings.contains(cdPath)){
				return FileSystemActionResult.fail(NO_SUCH_FILE_OR_DIRECTORY);
			}else {
				return FileSystemActionResult.success(currentPath.equals(ROOT_PATH) ? cdPath :  currentPath + cdPath);
			}
		}
	}
}
