package me.fearme.pokerbot.command.commands;

import me.fearme.pokerbot.command.Trigger;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Jorren Hendriks.
 */
public class Pong extends Trigger {

    public Pong() {
        super("pong");
        noPrefix();
    }

    @Override
    public void execute(String label, MessageReceivedEvent event) {
        event.getChannel().sendMessage("ping").queue();
    }
}
