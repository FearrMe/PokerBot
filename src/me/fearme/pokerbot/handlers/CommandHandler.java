package me.fearme.pokerbot.handlers;

import me.fearme.pokerbot.entities.command.AbstractCommand;
import me.fearme.pokerbot.entities.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FearMe on 12-2-2018.
 */
public class CommandHandler extends ListenerAdapter {

    List<AbstractCommand> commands = new ArrayList<>();

    private final String prefix;

    public CommandHandler(String prefix, AbstractCommand... commands) {
        this.prefix = prefix;
        addCommands(commands);
    }

    public void addCommands(AbstractCommand... commands) {
        for (AbstractCommand c : commands) {
            if(c instanceof Command)
                ((Command) c).setPrefix(prefix);
            this.commands.add(c);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay().toLowerCase();

        if (!msg.startsWith(prefix) || event.getAuthor().isBot()) {
            return;
        }

        for (AbstractCommand command : commands) {
            if (command.shouldRun(event))
                command.execute(event);
        }
    }
}