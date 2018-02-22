package me.fearme.pokerbot.util;

/**
 * @author Jorren Hendriks.
 */
public class Markdown {

    public static String italics(String contents) {
        return '*' + contents + '*';
    }

    public static String bold(String contents) {
        return "**" + contents + "**";
    }

    public static String underline(String contents) {
        return "__" + contents + "__";
    }

    public static String strikethrough(String contents) {
        return "~~" + contents + "~~";
    }

    public static String codeLine(String contents) {
        return '`' + contents + '`';
    }

    public static String codeBlock(String contents) {
        return "```" + contents + "```";
    }

    public static String codeBlock(String syntax, String contents) {
        return codeBlock(syntax + "\n" + contents);
    }

    public static String emoji(String contents) {
        return ':' + contents + ':';
    }


}
