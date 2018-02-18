package me.fearme.pokerbot.handlers;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FearMe on 12-2-2018.
 * <p>
 * TODO: nothing?
 */
public class MessageHandler extends ListenerAdapter {

    public static final int AUTO_FLUSH = 5;

    private final BufferedWriter outputWriter;

    private int flush = 0;

    public MessageHandler() {
        outputWriter = null;
    }

    public MessageHandler(String logFile) throws IOException {
        outputWriter = new BufferedWriter(new FileWriter(logFile, true));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                outputWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM HH:mm:ss");

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String dateString = DATE_FORMAT.format(new Date(System.currentTimeMillis()));

        String formattedMessage;

        if (event.isFromType(ChannelType.PRIVATE)) {
            User user = event.getPrivateChannel().getUser();
            formattedMessage = String.format("[%s] [PM][%s#%s] %s%s: %s", dateString,
                    user.getName(), user.getDiscriminator(),
                    event.getMember().getUser().isBot() ? "[BOT]" : "",
                    event.getAuthor().getName(), msg);
        } else {
            formattedMessage = String.format("[%s] [%s][%s] %s%s: %s", dateString,
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getMember().getUser().isBot() ? "[BOT]" : "",
                    event.getMember().getEffectiveName(), msg);
        }

        System.out.println(formattedMessage);

        if (outputWriter != null) {
            try {
                outputWriter.write(formattedMessage);
                outputWriter.newLine();

                if(++flush >= AUTO_FLUSH) {
                    outputWriter.flush();
                    flush = 0;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}