package me.fearme.pokerbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Jorren Hendriks.
 */
public abstract class Trigger {

    public abstract void execute(MessageReceivedEvent event);

}
