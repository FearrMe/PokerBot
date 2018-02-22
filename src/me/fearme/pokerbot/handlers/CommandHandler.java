package me.fearme.pokerbot.handlers;

import me.fearme.pokerbot.command.Command;
import me.fearme.pokerbot.command.Trigger;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

/**
 * @author Jorren Hendriks.
 */
public class CommandHandler extends ListenerAdapter {

    private String prefix;

    private Map<String, Command> commands;
    private List<Trigger> triggers;

    public CommandHandler(String prefix) {
        commands = new HashMap<>();
        triggers = new ArrayList<>();

        this.prefix = prefix;
    }

    public void addCommand(Command command) {
        boolean success = true;
        for (String label : command.getLabels()) {
            label = label.toLowerCase();
            String prefixed = prefix + label;
            if (commands.containsKey(prefixed)) {
                System.err.println("Command with the label " + label + " already registered");
                success = false;
            } else {
                commands.put(prefixed, command);
                if (!command.isPrefixRequired()) {
                    commands.put(label, command);
                }
            }
        }
        if (success) System.out.println("Succesfully registered the " + command + " command");
    }

    public void addCommands(Command... commands) {
        for (Command command : commands) {
            addCommand(command);
        }
    }

    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
        System.out.println("Succesfully registered the " + trigger + " trigger");
    }

    public void addTriggers(Trigger... triggers) {
        for (Trigger trigger : triggers) {
            addTrigger(trigger);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentDisplay().toLowerCase();

        String label = message.split(" ")[0];
        if (commands.containsKey(label.toLowerCase())) {
            Command command = commands.get(label.toLowerCase());
            if (!command.isCaseSensitive() || Arrays.asList(command.getLabels()).contains(label)) {
                command.execute(label, event);
                return;
            }
        } else if (label.toLowerCase().equals(prefix + "help")) {
            sendHelp(event);
            return;
        }

        // if no command is executed fire any triggers
        for (Trigger trigger : triggers) {
            for (String lbl : Arrays.asList(trigger.getLabels())) {
                String msg = trigger.isCaseSensitive() ? message : message.toLowerCase();
                if (msg.contains(lbl)) {
                    trigger.execute(lbl, event);
                }
            }
        }
    }

    private void sendHelp(MessageReceivedEvent event) {
        StringBuilder message = new StringBuilder();
        Set<Command> unique = new HashSet<Command>();
        unique.addAll(commands.values());
        for (Command command : unique) {
            message.append(command.toString());
            message.append(" - ");
            message.append(command.getDescription());
            message.append('\n');
        }
        event.getChannel().sendMessage(message).queue();
    }
}
