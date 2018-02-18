package me.fearme.pokerbot.entities.card;

import java.util.*;

import me.fearme.pokerbot.entities.card.Card.*;

/**
 * Created by FearMe on 11-2-2018.
 *
 * TODO: nothing?
 */
public class Deck {

    private long seed;

    private List<Card> cards = new ArrayList<>();
    private Random r = new Random();

    public Deck() {
        seed = System.currentTimeMillis();
        reset();
    }

    public void reset() {
        cards.clear();
        fill();
    }

    public void fill() {
        cards.addAll(Card.values());
    }

    public Card take() {
        seed = r.nextInt(Integer.MAX_VALUE);
        r.setSeed(++seed);
        return cards.remove(r.nextInt(cards.size()));
    }

    public int size() {
        return cards.size();
    }
}
