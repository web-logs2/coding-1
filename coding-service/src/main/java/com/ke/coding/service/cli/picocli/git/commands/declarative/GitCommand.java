package com.ke.coding.service.cli.picocli.git.commands.declarative;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.RunLast;

import com.ke.coding.service.cli.picocli.git.model.ConfigElement;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Command(name = "git")
@Component
public class GitCommand implements Runnable {

	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new GitCommand());
		commandLine.registerConverter(ConfigElement.class, ConfigElement::from);

		commandLine.parseWithHandler(new RunLast(), args);
	}

	@Override
	public void run() {
		System.out.println("The popular git command");
	}
}
