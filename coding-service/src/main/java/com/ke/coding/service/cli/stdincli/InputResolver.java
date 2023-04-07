package com.ke.coding.service.cli.stdincli;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.action.AbstractAction;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 12:10
 * @description:
 */
@Service
public class InputResolver {

	private final Scanner sc = new Scanner(System.in);

	@Autowired
	CommandCenter commandCenter;

	public void run() {
		System.out.print("root@xyl-shell:/$");
		boolean run = true;
		while (run) {
			String input = sc.nextLine().trim();
			if ("exit".equals(input)) {
				run = false;
			} else {
				String action = input.split(" ")[0];
				Command command = new Command();
				command.setAction(action);
				command.setOriginData(input);
				try {
					commandCenter.run(command);
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
