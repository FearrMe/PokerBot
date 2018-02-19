package me.fearme.pokerbot.entities.card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FearMe on 11-2-2018.
 * <p>
 * TODO: nothing?
 */
public class Card implements Comparable<Card> {

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public static List<Card> values() {
        List<Card> values = new ArrayList<>(); // 52 card deck

        for (Rank rank : Rank.values()) {
            if (rank == Rank.ACE_LOW) {
                continue;
            }

            for (Suit suit : Suit.values()) {
                values.add(new Card(rank, suit));
            }
        }

        return values;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toFancyString() {
        return rank.getId() + suit.getEmoji();
    }

    /**
     * 1 > 2 => 1
     * 1 = 2 => 0
     * 1 < 2 => -1
     */
    @Override
    public int compareTo(Card card) {
        return getRank().getValue() - card.getRank().getValue();
    }

    public int reverseCompareTo(Card card) {
        return -compareTo(card);
    }

    @Override
    public boolean equals(Object card) {
        return (card instanceof Card) && suit == ((Card) card).getSuit()  && ((Card) card).getRank() == rank;
    }

    @Override
    public int hashCode() {
        return (suit.hashCode() + rank.hashCode())/2;
    }
    /*@Override
    public boolean equals(Object o) {
        if (!(o instanceof Card))
            return false;

        Card c = (Card) o;
        return (rank == c.getRank() && suit == c.getSuit());
    }*/

    @Override
    public String toString() {
        return rank.getId() + suit.getId();
    }

    public enum Rank {

        ACE_LOW("1", 1),
        DEUCE("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("T", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
        ACE("A", 14);

        private final String id;
        private int value;

        Rank(String id, int value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Suit {
        HEARTS("h"), DIAMONDS("d"), CLUBS("c"), SPADES("s");

        private final String id;

        Suit(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getEmoji() {
            return String.format(":%s:", toString().toLowerCase());
        }
    }
}
