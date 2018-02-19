package me.fearme.pokerbot.command;

import me.fearme.pokerbot.command.Executable;

/**
 * @author Jorren Hendriks.
 */
public abstract class Trigger extends Executable {

    public Trigger(String label, String... labels) {
        super(label, labels);
    }

}
