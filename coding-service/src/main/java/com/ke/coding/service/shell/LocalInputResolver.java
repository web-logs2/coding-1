package com.ke.coding.service.shell;

import com.ke.coding.api.dto.cli.CommandContext;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.AbstractAction;
import com.ke.coding.service.command.ActionDispatcher;
import java.io.InputStream;
import java.util.Scanner;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 12:10
 * @description:
 */
public class LocalInputResolver {

	private final Scanner sc = new Scanner(System.in);

	ActionDispatcher actionDispatcher = new ActionDispatcher();

	@SneakyThrows
	public void run() {
		System.out.print("root@xyl-shell:/$");
		boolean run = true;
		while (run) {
			String input = sc.nextLine().trim();
			InputStream inputStream = IOUtils.toInputStream(input);
			System.setIn(inputStream);
			if ("exit".equals(input)) {
				run = false;
			} else {
				String action = input.split(" ")[0];
				CommandContext commandContext = new CommandContext();
				commandContext.setAction(action);
				commandContext.setOriginData(input);
				try {
					actionDispatcher.run(commandContext);
					System.out.println();
					System.out.print("root@xyl-shell:" + AbstractAction.currentPath + "$");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(ErrorCodeEnum.ACTION_ERROR.message());
					System.out.print("root@xyl-shell:" + AbstractAction.currentPath + "$");
				}
			}
		}
	}
}
