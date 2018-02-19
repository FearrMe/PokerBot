package me.fearme.pokerbot.command.commands;

import me.fearme.pokerbot.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Jorren Hendriks.
 */
public class Ping extends Command {

    public Ping() {
        super("ping");
        noPrefix();
    }

    @Override
    public void execute(String label, MessageReceivedEvent event) {
        event.getChannel().sendMessage("pong").queue();
    }
}
