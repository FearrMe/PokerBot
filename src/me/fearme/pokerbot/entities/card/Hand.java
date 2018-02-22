package me.fearme.pokerbot.entities.card;

import java.util.List;

/**
 * @author Jorren Hendriks.
 *
 * TODO maak dit
 */
public class Hand {

    private List<Card> cards;

    public Hand(List<Card> cards) {
        this.cards = cards;
    }

    public static Hand bestHand(List<Card> cards) {
        int pairs = (int) cards.stream().filter(c -> cards.stream().filter(d ->
            c.getRank() == d.getRank()).count() == 2).count();
        boolean triples = (int) cards.stream().filter(c -> cards.stream().filter(d ->
            c.getRank() == d.getRank()).count() == 3).count() == 1;
        boolean fours = (int) cards.stream().filter(c -> cards.stream().filter(d ->
            c.getRank() == d.getRank()).count() == 4).count() == 1;
        boolean flush = cards.stream().filter(c -> cards.stream().filter(d ->
            c.getSuit() == d.getSuit()).count() == 5).count() == 1;

        return null;

    }
}
