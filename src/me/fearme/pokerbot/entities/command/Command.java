package me.fearme.pokerbot.entities.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by FearMe on 12-2-2018.
 */
public abstract class Command extends AbstractCommand {

    private String prefix;

    public Command(String command) {
        super(command);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public abstract void execute(MessageReceivedEvent event);

    @Override
    public boolean shouldRun(MessageReceivedEvent event) {
        return getMessageWithoutPrefix(event.getMessage().getContentDisplay()).startsWith(getCommand());
    }

    public String getMessageWithoutPrefix(String message) {
        return message.toLowerCase().substring(prefix.length()).trim();
    }

    public String[] getArgs(String msg) {
        return msg.substring(prefix.length() + getCommand().length()).trim().split(" ");
    }
}
