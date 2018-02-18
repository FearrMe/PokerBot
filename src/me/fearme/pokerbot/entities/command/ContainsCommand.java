package me.fearme.pokerbot.entities.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by FearMe on 12-2-2018.
 */
public abstract class ContainsCommand extends AbstractCommand {

    private boolean caseSensitive;

    public ContainsCommand(String command, boolean caseSensitive) {
        super(command);
        this.caseSensitive = caseSensitive;
    }

    public ContainsCommand(String command) {
        this(command, false);
    }

    public abstract void execute(MessageReceivedEvent event);

    @Override
    public boolean shouldRun(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        msg = caseSensitive ? msg : msg.toLowerCase();
        return msg.contains(getCommand());
    }
}
