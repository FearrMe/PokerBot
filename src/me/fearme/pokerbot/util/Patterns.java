package me.fearme.pokerbot.util;

/**
 * @author Jorren Hendriks.
 */
public class Patterns {

    /**
     * Make sure the matched pattern is not surrounded by non-whitespace characters
     *
     * @param pattern The pattern to modify.
     * @return A modified pattern which prevents non-whitespace characters adjacent to the pattern.
     */
    public static String clear(String pattern) {
        return "(?<![\\S])" + pattern + "(?![\\S])";
    }
}
