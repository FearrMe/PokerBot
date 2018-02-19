package me.fearme.pokerbot.entities;

import me.fearme.pokerbot.entities.card.Card;
import me.fearme.pokerbot.entities.card.Card.Suit;
import me.fearme.pokerbot.entities.card.Deck;
import me.fearme.pokerbot.util.Evaluator;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by FearMe on 11-2-2018.
 * <p/>
 * TODO: hand evaluator
 */
public class Hand implements Comparable<Hand> {

    private static final int MAX_CARDS_IN_HAND = 5;

    private final Hands rank;
    private final List<Card> relevant;
    private final List<Card> kickers;


    private Hand(Hands rank, List<Card> relevant, List<Card> kickers) {
        this.rank = rank;
        this.relevant = relevant;
        this.kickers = kickers;
    }

    public static Hand getHand(List<Card> cards) {
        List<Card> cardsCopy = new ArrayList<>(cards);
        cardsCopy.sort(Collections.reverseOrder());

        Hands handRank = null;

        for (int i = 0; i < Hands.values().length; i++) {
            Hands h = Hands.get(i);
            if (h.isValid(cards)) {
                handRank = h;
                break;
            }
        }

        List<Card> relevantCards = handRank.getRelevantCards(cardsCopy);
        List<Card> kickers = handRank.getKickers(cardsCopy);

        return new Hand(handRank, relevantCards, kickers);
    }

    public Hands getRank() {
        return rank;
    }

    /**
     * 1 > 2 => 1
     * 1 = 2 => 0
     * 1 < 2 => -1
     */
    @Override
    public int compareTo(Hand hand) {
        if (rank.compareTo(hand.getRank()) != 0) {
            return rank.compareTo(hand.getRank());
        }

        int compare = 0;

        switch (rank) {
            case HIGH_CARD:
            case ONE_PAIR:
            case THREE_OF_A_KIND:
            case STRAIGHT:
            case FLUSH:
            case FOUR_OF_A_KIND:
            case STRAIGHT_FLUSH:
                // only highest card matters here, if equal compare kickers
                compare = relevant.get(0).compareTo(hand.getRelevantCards().get(0));

                for (int i = 0; compare == 0 && i < kickers.size(); i++) {
                    compare = kickers.get(i).compareTo(hand.getKickers().get(i));
                }
                return compare;
            case TWO_PAIR:
                // highest pair matters most
                compare = relevant.get(0).compareTo(hand.getRelevantCards().get(0));

                if (compare == 0) {
                    compare = relevant.get(2).compareTo(hand.getRelevantCards().get(2));

                    if (compare == 0) {
                        compare = kickers.get(0).compareTo(hand.getKickers().get(0));
                    }
                }

                return compare;
            case FULL_HOUSE:
                // three of a kind part takes priority, but might not necessarily be the highest of the two
                List<Card> highestRelevantMe = Hands.THREE_OF_A_KIND.getRelevantCards(relevant);
                List<Card> highestRelevantThem = Hands.THREE_OF_A_KIND.getRelevantCards(hand.getRelevantCards());
                compare = highestRelevantMe.get(0).compareTo(highestRelevantThem.get(0));

                if (compare == 0) {
                    List<Card> lowestRelevantMe = relevant.stream()
                            .filter(c -> !highestRelevantMe.contains(c)).collect(Collectors.toList());
                    List<Card> lowestRelevantThem = hand.getRelevantCards().stream()
                            .filter(c -> !highestRelevantThem.contains(c)).collect(Collectors.toList());
                    compare = lowestRelevantMe.get(0).compareTo(lowestRelevantThem.get(0));
                }

                return compare;
            case ROYAL_FLUSH:
                // should not get hit ever
        }

        return compare;
    }

    public List<Card> getRelevantCards() {
        return relevant;
    }

    public List<Card> getKickers() {
        return kickers;
    }

    public enum Hands {

        /**
         * 100% done
         */
        HIGH_CARD(9, 1, (cards -> cards.subList(0, 1))),

        /**
         * 100% done
         */
        ONE_PAIR(8, 2, cards -> {
            List<Card> relevantCards = new ArrayList<>();

            for (Card c1 : cards) {
                relevantCards = cards.stream().filter(c2 -> c1.compareTo(c2) == 0).collect(Collectors.toList());
                if (relevantCards.size() >= 2) // pair found
                    break;
                else
                    relevantCards.clear();
            }

            return relevantCards.size() >= 2 ? relevantCards.subList(0, 2) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        TWO_PAIR(7, 4, cards -> {
            List<Card> relevantCards = new ArrayList<>();
            List<Card> tempCards;

            for (Card c1 : cards) {
                tempCards = cards.stream().filter(c2 -> c1.compareTo(c2) == 0).collect(Collectors.toList());
                if (tempCards.size() >= 2) { // at least one pair found
                    relevantCards.addAll(tempCards);
                    relevantCards = relevantCards.stream().distinct().collect(Collectors.toList()); // clear dupes
                    if (relevantCards.size() >= 4) // two pair found
                        break;
                }
                tempCards.clear();
            }

            return relevantCards.size() >= 4 ? relevantCards.subList(0, 4) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        THREE_OF_A_KIND(6, 3, cards -> {
            List<Card> relevantCards = new ArrayList<>();

            for (Card c1 : cards) {
                relevantCards = cards.stream().filter(c2 -> c1.compareTo(c2) == 0).collect(Collectors.toList());
                if (relevantCards.size() >= 3)
                    break;
                else
                    relevantCards.clear();
            }

            return relevantCards.size() >= 3 ? relevantCards.subList(0, 3) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        STRAIGHT(5, 5, cards -> {
            List<Card> relevantCards = new ArrayList<>();
            List<Card> tempCards = new ArrayList<>(cards);

            if (tempCards.get(0).getRank() == Card.Rank.ACE) {
                Card highAce = tempCards.get(0);
                Card lowAce = new Card(Card.Rank.ACE_LOW, highAce.getSuit());
                tempCards.add(lowAce);
            }

            int tries = tempCards.size() - 4;

            for (int i = 0; i < tries; i++) {
                Card c1 = tempCards.get(i);
                relevantCards.add(c1);

                int offset = 0;
                for (int j = 1; (i + j) < tempCards.size(); j++) {
                    Card c2 = tempCards.get(i + j);
                    if (c2.compareTo(tempCards.get(i + j - 1)) == 0) {
                        offset++;
                        continue;
                    }
                    //System.out.printf("%s %d %s\n", c1, c1.compareTo(c2), c2);
                    if (c1.compareTo(c2) == (j - offset)) { // diff between c1 and c2 should be j ranks(minus offset)
                        relevantCards.add(c2);
                        if (relevantCards.size() >= 5) {
                            break;
                        }
                    } else {
                        i += (j - 1);
                        relevantCards.clear();
                        break;
                    }
                }
                if (relevantCards.size() >= 5) {
                    break;
                }

                relevantCards.clear();
            }

            return relevantCards.size() >= 5 ? relevantCards.subList(0, 5) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        FLUSH(4, 5, cards -> {
            List<Card> relevantCards = new ArrayList<>();

            for (Suit s : Suit.values()) {
                relevantCards = cards.stream().filter(c -> s == c.getSuit()).collect(Collectors.toList());
                if (relevantCards.size() >= 5) { // flush found
                    break;
                } else {
                    relevantCards = new ArrayList<>();
                }
            }

            return relevantCards.size() >= 5 ? relevantCards.subList(0, 5) : new ArrayList<>();
        }),


        FULL_HOUSE(3, 5, cards -> {
            List<Card> relevantCards = THREE_OF_A_KIND.getRelevantCards(cards);
            List<Card> tempCards = new ArrayList<>(cards);

            tempCards.removeAll(relevantCards);
            relevantCards.addAll(ONE_PAIR.getRelevantCards(tempCards));

            return relevantCards.size() >= 5 ? relevantCards.subList(0, 5) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        FOUR_OF_A_KIND(2, 4, cards -> {
            List<Card> relevantCards = new ArrayList<>();

            for (Card c1 : cards) {
                relevantCards = cards.stream().filter(c2 -> c1.compareTo(c2) == 0).distinct().collect(Collectors.toList());
                if (relevantCards.size() >= 4)
                    break;
                else
                    relevantCards.clear();
            }

            return relevantCards.size() >= 4 ? relevantCards.subList(0, 4) : new ArrayList<>();
        }),


        STRAIGHT_FLUSH(1, 5, cards -> {
            final List<Card> flushCards = new ArrayList<>();
            List<Card> relevantCards = new ArrayList<>();

            for (Suit s : Suit.values()) {
                flushCards.addAll(cards.stream().filter(c -> s == c.getSuit()).collect(Collectors.toList()));
                if (flushCards.size() >= 5) { // flush found
                    break;
                } else {
                    flushCards.clear();
                }
            }

            if (flushCards.size() >= 5) {
                relevantCards = STRAIGHT.getRelevantCards(flushCards);
            }

            return relevantCards.size() >= 5 ? relevantCards.subList(0, 5) : new ArrayList<>();
        }),

        /**
         * 100% done
         */
        ROYAL_FLUSH(0, 5, cards -> {
            List<Card> relevantCards = STRAIGHT_FLUSH.getRelevantCards(cards);
            return (relevantCards.size() == 5 && relevantCards.get(0).getRank() == Card.Rank.ACE) ?
                    relevantCards : new ArrayList<>();
        });

        public final int rank;
        private int relevantNo;
        private Evaluator<List<Card>> evaluator;

        Hands(int rank, int relevantNo, Evaluator<List<Card>> evaluator) {
            this.rank = rank;
            this.relevantNo = relevantNo;
            this.evaluator = evaluator;
        }

        public boolean isValid(List<Card> cards) {
            return cards.size() >= relevantNo && getRelevantCards(cards).size() == relevantNo;
        }

        public List<Card> getRelevantCards(List<Card> cards) {
            return evaluator.evaluate(cards);
        }

        public List<Card> getKickers(List<Card> cards) {
            List<Card> relevantCards = getRelevantCards(cards);

            return cards.stream().filter(c -> !relevantCards.contains(c))
                    .collect(Collectors.toList())
                    .subList(0, Integer.max(Integer.min(cards.size(), MAX_CARDS_IN_HAND) - relevantNo, 0));
        }

        public static Hands get(int rank) {
            for (Hands h : values()) {
                if (h.rank == rank)
                    return h;
            }
            return null;
        }
    }

    public static void main(String[] args) {
        /*Hands h = Hands.STRAIGHT_FLUSH;
        System.out.println(h.compareTo(Hands.ROYAL_FLUSH)); // -1
        System.out.println(h.compareTo(Hands.STRAIGHT_FLUSH)); // 0
        System.out.println(h.compareTo(Hands.HIGH_CARD)); // 1*/

        /*HashMap<Hands, Integer> map = new HashMap<>();
        HashMap<Card.Rank, Integer> rankMap = new HashMap<>();

        for (Hands h : Hands.values()) {
            map.put(h, 0);
        }

        for (Card.Rank r : Card.Rank.values()) {
            rankMap.put(r, 0);
        }

        List<List<Card>> combinations = new ArrayList<>();

        Deck deck = new Deck();
        deck.reset();

        List<Card> cards1 = new ArrayList<>();

        cards1.add(new Card(Card.Rank.ACE, Suit.CLUBS));
        cards1.add(new Card(Card.Rank.KING, Suit.HEARTS));
        cards1.add(new Card(Card.Rank.TEN, Suit.SPADES));
        cards1.add(new Card(Card.Rank.THREE, Suit.SPADES));
        cards1.add(new Card(Card.Rank.QUEEN, Suit.HEARTS));
        cards1.sort(Card::reverseCompareTo);

        combinations.add(cards1);

        List<Card> cards2 = new ArrayList<>();

        cards2.add(new Card(Card.Rank.ACE, Suit.CLUBS));
        cards2.add(new Card(Card.Rank.TEN, Suit.SPADES));
        cards2.add(new Card(Card.Rank.KING, Suit.HEARTS));
        cards2.add(new Card(Card.Rank.THREE, Suit.SPADES));
        cards2.add(new Card(Card.Rank.QUEEN, Suit.HEARTS));
        cards2.sort(Card::reverseCompareTo);

        combinations.add(cards1);

        System.out.println(cards1.hashCode() + " + " + cards1.equals(cards2) + " + " + cards2.hashCode());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
        Future future = threadExecutor.submit(() -> {
                    while (combinations.size() < 2598960) {
                        deck.reset();
                        List<Card> cards = new ArrayList<>();

                        for (int j = 0; j < 5; j++) {
                            cards.add(deck.take());
                        }

                        cards.sort(Card::reverseCompareTo);

                        for (List<Card> combination : combinations) {
                            List<Card> temp = new ArrayList<>(combination);
                            temp.sort(Card::reverseCompareTo);
                            if (combination.equals(cards)) {
                                System.out.printf("found dupe with %d left\n", (2598960 - combinations.size()));
                            } else {
                                combinations.add(cards);
                                break;
                            }
                        }

                        if (combinations.size() % 100000 == 0) {
                            System.out.printf("size: %d, %d left\n", combinations.size(), (2598960 - combinations.size()));
                        }
                    }
                });

        while(!future.isDone())
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println("found all");

        for (List<Card> cards : combinations) {
            cards.sort(Card::reverseCompareTo);
            Hand hand = Hand.getHand(cards);

            map.put(hand.rank, map.get(hand.rank) + 1);
        }

        for (Hands h : Hands.values()) {
            int occurence = map.get(h);
            System.out.printf("%06d | %s\n", occurence, h.toString());
        }


        if (true) {
            return;
        }


        double n = 50000000000D;
        int interval = 1000000;

        for (int i = 0; i <= n; i++) {
            deck.reset();
            List<Card> cards = new ArrayList<>();

            for (int j = 0; j < 5; j++) {
                Card c = deck.take();
                rankMap.put(c.getRank(), rankMap.get(c.getRank()) + 1);
                cards.add(c);
            }

            cards.sort(Collections.reverseOrder());

            Hand hand = Hand.getHand(cards);

            map.put(hand.rank, map.get(hand.rank) + 1);

            if (i != 0 && i % interval == 0) {
                System.out.println(i + "/" + n + " | " + ((i / (1.0 * n)) * 100) + "%");
                for (Hands h : Hands.values()) {
                    int occurence = map.get(h);
                    double percent = occurence / (1.0 * (i + 1)) * 100;
                    System.out.printf("%.7f\n", percent);
                }
                System.out.println();
                for (Card.Rank r : Card.Rank.values()) {
                    int occurence = rankMap.get(r);
                    double percent = occurence / (1.0 * (n * 7)) * 100;
                    System.out.printf("%.4f%% %06d | %s\n", percent, occurence, r.toString());
                }
                System.out.println();
            }
        }

        int m = 5;
        long binomial = factorial(52).divide(
                (factorial(m).multiply(factorial(52-m)))).longValue();

        System.out.println(binomial);

        List<Card> cards = new Deck().getCards();

        List<List<Card>> combinations = new ArrayList<>();

        for (int i = 0; i < binomial; i++) { // loop till all possible combinations are done
            List<Card> combination = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                combination.add(cards.get(((i * m) + j) % 51));
            }
            combination.sort(Card::reverseCompareTo);
            combinations.add(combination);
            if(i % 100000 == 0) {
                System.out.println(i);
            }
        }

        System.out.println(combinations.size());
        System.out.println(combinations.size() == combinations.stream().distinct().count());
        System.out.println(combinations.stream().distinct().count());

        List<Card> cards = new ArrayList<>();

        cards.add(new Card(Card.Rank.ACE, Suit.CLUBS));
        cards.add(new Card(Card.Rank.KING, Suit.HEARTS));
        cards.add(new Card(Card.Rank.TEN, Suit.SPADES));
        cards.add(new Card(Card.Rank.ACE, Suit.SPADES));
        cards.add(new Card(Card.Rank.QUEEN, Suit.HEARTS));
        cards.add(new Card(Card.Rank.JACK, Suit.HEARTS));
        cards.add(new Card(Card.Rank.ACE, Suit.DIAMONDS));

        cards.sort(Collections.reverseOrder());

        Hand hand = Hand.getHand(cards);
        map.put(hand.rank, map.get(hand.rank) + 1);

        System.out.print(hand.getRank().toString() + " | ");
        hand.getRelevantCards().forEach(System.out::print);
        System.out.print(" | ");
        hand.getKickers().forEach(System.out::print);
        System.out.print(" || ");
        System.out.println("ACE HIGH");
        System.out.println();

        cards = new ArrayList<>();

        cards.add(new Card(Card.Rank.FIVE, Suit.HEARTS));
        cards.add(new Card(Card.Rank.FOUR, Suit.HEARTS));
        cards.add(new Card(Card.Rank.THREE, Suit.HEARTS));
        cards.add(new Card(Card.Rank.DEUCE, Suit.SPADES));
        cards.add(new Card(Card.Rank.ACE, Suit.SPADES));
        cards.add(new Card(Card.Rank.THREE, Suit.CLUBS));
        cards.add(new Card(Card.Rank.THREE, Suit.DIAMONDS));

        cards.sort(Collections.reverseOrder());

        hand = Hand.getHand(cards);
        map.put(hand.rank, map.get(hand.rank) + 1);

        System.out.print(hand.getRank().toString() + " | ");
        hand.getRelevantCards().forEach(System.out::print);
        System.out.print(" | ");
        hand.getKickers().forEach(System.out::print);
        System.out.print(" || ");
        System.out.println("ACE LOW THREE THREE");
        System.out.println();

        for (Hands h : Hands.values()) {
            int occurence = map.get(h);
            double percent = occurence / (1.0 * n) * 100;
            String str = percent < 10 ? "0" : "";
            System.out.printf(str + "%.4f%% %06d | %s\n", percent, occurence, h.toString());
        }

        for (Card.Rank r : Card.Rank.values()) {
            int occurence = rankMap.get(r);
            double percent = occurence / (1.0 * (n * 7)) * 100;
            System.out.printf("%.4f%% %06d | %s\n", percent, occurence, r.toString());
        }

        Hands[] h = Hands.values();

        for (int i = h.length - 1; i >= 0; i--) {
            System.out.println(h[i]);
        }


        cards.add(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS));
        cards.add(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        cards.add(new Card(Card.Rank.NINE, Card.Suit.CLUBS));
        cards.add(new Card(Card.Rank.ACE, Card.Suit.DIAMONDS));
        cards.add(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        cards.add(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        cards.add(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        cards.subList(0, 5).forEach(System.out::println);*/
    }

    static BigInteger factorial(int n) {
        BigInteger ret = BigInteger.ONE;
        for (long i = n; i > 0; i--) {
            ret = ret.multiply(BigInteger.valueOf(i));
        }
        return ret;
    }
}
