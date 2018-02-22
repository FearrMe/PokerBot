package me.fearme.pokerbot.command.commands;

import me.fearme.pokerbot.command.Trigger;
import me.fearme.pokerbot.util.Patterns;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.fearme.pokerbot.util.Markdown.*;

/**
 * @author Jorren Hendriks.
 */
public class ImperialToMetric extends Trigger {

    private final static DecimalFormat DECIMAL = new DecimalFormat("##.###");
    private final static Pattern INCH_VALUE = Pattern.compile(Patterns.clear("(-?[0-9]*([.,][0-9]+)?) in(ch)?"));

    @Override
    public void execute(MessageReceivedEvent event) {
        Matcher regex = INCH_VALUE.matcher(event.getMessage().toString());
        while(regex.find()) {
            double value = Double.parseDouble(regex.group(1));
            event.getChannel().sendMessage(codeLine(String.format("%s inch = %s",
                    DECIMAL.format(value), simpleMetric(value * 0.0254d))));
        }
    }

    private static String simpleMetric(double meters) {
        if (meters < 0.01d) {
            return DECIMAL.format(1000 * meters) + " mm";
        } else if (meters < 1d) {
            return DECIMAL.format(100 * meters) + " cm";
        } else if (meters > 1000) {
            return DECIMAL.format(meters / 1000d) + " km";
        } else {
            return DECIMAL.format(meters) + " m";
        }
    }
}
