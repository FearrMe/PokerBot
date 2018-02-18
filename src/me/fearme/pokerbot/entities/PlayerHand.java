package me.fearme.pokerbot.entities;

import me.fearme.pokerbot.entities.card.Card;

/**
 * Created by FearMe on 11-2-2018.
 */
public class PlayerHand {

    private final Card left;
    private final Card right;

    public PlayerHand(Card left, Card right) {
        this.left = left;
        this.right = right;
    }
}
