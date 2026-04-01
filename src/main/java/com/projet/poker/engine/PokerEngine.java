package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import main.java.com.projet.poker.model.game.PlayerSession;
import main.java.com.projet.poker.model.game.Table;

public class PokerEngine {

    /*
     * Le moteur de jeu du poker gère la logique du jeu, les règles,
     * les tours de mise, la distribution des cartes, etc.
     */

    public PokerEngine() {}

    public void distribute(Table table, Deck deck) {
        for (PlayerSession player: table.getActivePlayers()) {
            player.getHoleCards().add(deck.drawCard());
            player.getHoleCards().add(deck.drawCard());
        }
    }

    public List<Card> sortCards(PlayerSession player, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(player.getHoleCards());
        allCards.addAll(communityCards);

        CardComparator cmp = new CardComparator();
        Collections.sort(allCards, cmp);
            
        return allCards;
    }

    public HashMap<CardValue, Integer> countCardValues(List<Card> cards) {
        HashMap<CardValue, Integer> map = new HashMap<>();

        for (Card c : cards) {
            map.merge(c.getCardValue(), 1, Integer::sum);
        }

        return map;
    }

    public HashMap<CardColor, Integer> countCardColors(List<Card> cards) {
        HashMap<CardColor, Integer> map = new HashMap<>();

        for (Card c : cards) {
            map.merge(c.getCardColor(), 1, Integer::sum);
        }

        return map;
    }

    public boolean isQuinteFlush(HandData hand) {
        return false;
    }

    public boolean isCarre(HandData hand) {
        return false;
    }

    public boolean isFull(HandData hand) {
        return false;
    }

    public boolean isFlush(HandData hand) {
        return false;
    }

    public boolean isQuinte(HandData hand) {
        return false;
    }

    public boolean isBrelan(HandData hand) {
        return false;
    }

    public boolean isDoublePaire(HandData hand) {
        return false;
    }

    public boolean isPaire(HandData hand) {
        return false;
    }

    public boolean isCarteHaute(HandData hand) {
        return false;
    }
    
    public void evaluateHand(HandData hand) {
        if (isQuinteFlush(hand)){ hand.setType(HandType.QUINTE_FLUSH); return; }
        if (isCarre(hand)){ hand.setType(HandType.CARRE); return; }
        if (isFull(hand)){ hand.setType(HandType.FULL); return; }
        if (isFlush(hand)){ hand.setType(HandType.FLUSH); return; }
        if (isQuinte(hand)){ hand.setType(HandType.QUINTE); return; }
        if (isBrelan(hand)){ hand.setType(HandType.BRELAN); return; }
        if (isDoublePaire(hand)){ hand.setType(HandType.DOUBLE_PAIRE); return; }
        if (isPaire(hand)){ hand.setType(HandType.PAIRE); return; }
        if (isCarteHaute(hand)){ hand.setType(HandType.CARTE_HAUTE); return; }
    }

    public int evaluateSinglePlayer(PlayerSession player, List<Card> community) {
        List<Card> sortedCards = sortCards(player, community);
        HashMap<CardValue, Integer> cardValues = countCardValues(sortedCards);
        HashMap<CardColor, Integer> cardColors = countCardColors(sortedCards);

        HandData hand = new HandData(cardValues, cardColors, sortedCards);
        evaluateHand(hand);
        
        return 0;
    }

    public static void main(String[] args) {
        Table t = new Table();
        PokerEngine engine = new PokerEngine();
        Deck deck = t.getHand().getDeck();

        for (int i = 0; i < 5; i++) {
            t.addPlayer(new PlayerSession(i));
        }

        deck.shuffle();
        engine.distribute(t, deck);
        t.showPlayerHands();
    }
    
}
