package me.fearme.pokerbot.command;

import me.fearme.pokerbot.command.Executable;

/**
 * @author Jorren Hendriks.
 */
public abstract class Command extends Executable {

    public Command(String label, String... labels) {
        super(label, labels);
    }

    public String getDescription() {
        return "";
    }

}
