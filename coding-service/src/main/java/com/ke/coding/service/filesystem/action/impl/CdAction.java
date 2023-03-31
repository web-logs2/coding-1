package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
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
	LlAction llAction;

	@Override
	public FileSystemResult run(Command command) {
		String currentPath = command.getCurrentPath();
		String cdPath = command.getParams().get(0);
		if ("..".equals(cdPath)) {
			if (currentPath.equals(ROOT_PATH)) {
				return FileSystemResult.success(ROOT_PATH);
			} else {
				String result = "";
				String[] split = command.getCurrentPath().split(PATH_SPLIT);
				for (int i = 0; i < split.length - 1; i++) {
					result = ROOT_PATH + split[i];
				}
				return FileSystemResult.success(result);
			}
		} else if (cdPath.equals(ROOT_PATH)) {
			return FileSystemResult.success(ROOT_PATH);
		} else {
			FileSystemResult result = llAction.run(Command.build(currentPath));
			String data = result.getData();
			List<DirectoryEntrySubInfo> directoryEntrySubInfos = JsonUtils.parseStr2List(data, DirectoryEntrySubInfo.class);
			cdPath = cdPath.contains("/") ? cdPath : ROOT_PATH + cdPath;
			for (DirectoryEntrySubInfo directoryEntrySubInfo : directoryEntrySubInfos) {
				if (directoryEntrySubInfo.getFileName().contains(cdPath)) {
					return FileSystemResult.success(currentPath.equals(ROOT_PATH) ? cdPath : currentPath + cdPath);
				}
			}
			return FileSystemResult.fail(NO_SUCH_FILE_OR_DIRECTORY);

		}
	}
}
