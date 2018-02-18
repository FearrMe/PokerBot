package me.fearme.pokerbot.entities.command;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by FearMe on 12-2-2018.
 */
public abstract class AbstractCommand {

    private String command;

    public AbstractCommand(String command) {
        this.command = command.toLowerCase();
    }

    public abstract void execute(MessageReceivedEvent event);

    public abstract boolean shouldRun(MessageReceivedEvent event);

    public String getCommand() {
        return command;
    }

    public static void queueMessage(MessageReceivedEvent event, Message msg) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            event.getAuthor().openPrivateChannel().queue(c -> c.sendMessage(msg).queue(), Throwable::printStackTrace);
        } else {
            event.getChannel().sendMessage(msg).queue();
        }
    }

    public static void queueMessage(MessageReceivedEvent event, String msg) {
        queueMessage(event, new MessageBuilder(msg).build());
    }
}
