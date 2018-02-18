package me.fearme.pokerbot.entities.command.commands;

import me.fearme.pokerbot.entities.command.ContainsCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help extends ContainsCommand {

    public static final String COMMAND = "help";

    public Help() {
        super(COMMAND);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        System.out.println("help!!!!");
    }
}
