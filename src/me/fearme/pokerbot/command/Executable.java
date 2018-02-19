package me.fearme.pokerbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;

/**
 * @author Jorren Hendriks.
 */
public abstract class Executable {

    private final String[] labels;

    private boolean requirePrefix = true;
    private boolean ignoreCase = true;

    public Executable(String label, String... labels) {
        this.labels = (String[]) Arrays.asList(label, labels).toArray();
    }

    public Executable noPrefix() {
        this.requirePrefix = false;
        return this;
    }

    public boolean isPrefixRequired() {
        return requirePrefix;
    }

    public Executable caseSensitive() {
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

    @Override
    public String toString() {
        return labels[0];
    }
}
