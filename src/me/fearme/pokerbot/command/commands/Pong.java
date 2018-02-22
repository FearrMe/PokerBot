package me.fearme.pokerbot.command.commands;

import me.fearme.pokerbot.command.Trigger;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Jorren Hendriks.
 */
public class Pong extends Trigger {

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("ping").queue();
    }

}
