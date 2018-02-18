package me.fearme.pokerbot.entities.command.commands;

import me.fearme.pokerbot.entities.command.ContainsCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Pong extends ContainsCommand {

    public static final String COMMAND = "pong";

    public Pong() {
        super(COMMAND);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("ping").queue();
    }
}
