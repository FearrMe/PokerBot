package me.fearme.pokerbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;

/**
 * @author Jorren Hendriks.
 */
public abstract class Command {

    private final String[] labels;

    private boolean requirePrefix = true;
    private boolean ignoreCase = true;

    public Command(String label, String... labels) {
        this.labels = (String[]) Arrays.asList(label, labels).toArray();
    }

    public Command noPrefix() {
        this.requirePrefix = false;
        return this;
    }

    public boolean isPrefixRequired() {
        return requirePrefix;
    }

    public Command caseSensitive() {
        this.ignoreCase = false;
        return this;
    }

    public boolean isCaseSensitive() {
        return !this.ignoreCase;
    }

    public String[] getLabels() {
        return labels;
    }

    public abstract void execute(String label, MessageReceivedEvent event);

    public String getDescription() {
        return null;
    }

    @Override
    public String toString() {
        return labels[0];
    }

}
