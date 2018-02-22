package me.fearme.pokerbot.entities.card;

import java.util.*;

import me.fearme.pokerbot.entities.card.Card.*;

import static com.sun.deploy.security.X509DeployTrustManager.reset;

/**
 * Created by FearMe on 11-2-2018.
 *
 * TODO: nothing?
 */
public class Deck {

    private long seed;

    private List<Card> cards;

    public Deck() {
        reset();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void reset() {
        cards = Card.values();
        shuffle();
    }

    public Card take() {
        if (size() > 0) {
            return cards.remove(0);
        }

        return null;
    }

    public int size() {
        return cards.size();
    }
}
