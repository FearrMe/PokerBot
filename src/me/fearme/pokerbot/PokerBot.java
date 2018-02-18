package me.fearme.pokerbot;

import me.fearme.pokerbot.handlers.CommandHandler;
import me.fearme.pokerbot.entities.command.commands.Ping;
import me.fearme.pokerbot.entities.command.commands.Pong;
import me.fearme.pokerbot.handlers.MessageHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

/**
 * Created by FearMe on 11-2-2018.
 */
public class PokerBot {

    public static JDA jda;
    public static CommandHandler commandHandler = new CommandHandler("p!");
    public static CommandHandler noPrefix = new CommandHandler("");

    public static void main(String[] args) throws LoginException, InterruptedException {
        jda = new JDABuilder(AccountType.BOT).setToken("NDEyMDcxNTc1NTU1Mjc2ODAw.DWE7Zg.lijqmNy-mmNSHmjHT1M2aa84Juo").buildBlocking();
        jda.getPresence().setGame(Game.playing("Poker"));

        try {
            jda.addEventListener(new MessageHandler(args[0]));
            System.out.println("LOGGING TO " + args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            jda.addEventListener(new MessageHandler());
            System.out.println("LOGGING OFF");
        }

        jda.addEventListener(new MessageHandler());

        commandHandler.addCommands(new Ping());
        noPrefix.addCommands(new Ping(), new Pong());

        jda.addEventListener(commandHandler, noPrefix);

        System.out.println("-------------------------------------");
    }
}
