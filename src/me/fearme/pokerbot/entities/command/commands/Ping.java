package me.fearme.pokerbot.entities.command.commands;

import me.fearme.pokerbot.entities.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Ping extends Command {

    public static final String COMMAND = "ping";

    public Ping() {
        super(COMMAND);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("pong").queue();
    }
}
