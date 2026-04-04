package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import main.java.com.projet.poker.model.game.PlayerSession;
import main.java.com.projet.poker.model.game.Table;

public class PokerEngine {

    /*
     * Le moteur de jeu du poker gère la logique du jeu, les règles,
     * les tours de mise, la distribution des cartes, etc.
     */

    private static final int MAX_HAND = 5;

    public PokerEngine() {}

    public Card getKeyByValue(HandData hand, Integer value) {
        int i = 0;
        
        for (Map.Entry<CardValue, Integer> entry : hand.getCountCardValues().entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return hand.getSortedCards().get(i);
            }
            i++;
        }
        return null;
    }

    // A corriger: n'ajoute pas les 3 cartes
    public List<Card> getKeysByValue(HandData hand, Integer value, int max) {
        int i = 0, n = 0;
        List<Card> keys = new ArrayList<>();
        for (Map.Entry<CardValue, Integer> entry : hand.getCountCardValues().entrySet()) {
            if (Objects.equals(value, entry.getValue()) && n < max) {
                keys.add(hand.getSortedCards().get(i));
                n++;
            }
            i++;
        }
        return keys;
    }

    public void updateBestWithKickers(List<Card> currentBest, List<Card> ordered) {
        int numberOfKickers = MAX_HAND - currentBest.size();
        if (numberOfKickers == 0) return;

        List<Card> temp = new ArrayList<>();

        for (int i = 0; i < ordered.size() && (temp.size() < numberOfKickers); i++) {
            Card currentOrdered = ordered.get(i);
            if (!currentBest.contains(currentOrdered)) {
                temp.add(currentOrdered);
            }
        }

        currentBest.addAll(temp);
    }

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

    public List<Card> isCarre(HandData hand) {
        List<Card> best = getKeysByValue(hand, 4, MAX_HAND);

        if (best.size() > 0) {
            updateBestWithKickers(best, hand.getSortedCards());
            return best;
        } else {
            return null;
        }
    }

    public List<Card> isFull(HandData hand) {
        List<Card> best = getKeysByValue(hand, 3, MAX_HAND);
        if (best.size() < MAX_HAND) best.addAll(getKeysByValue(hand, 2, MAX_HAND - best.size()));
        
        if (best.size() == MAX_HAND) {
            return best;
        } else {
            return null;
        }
        // return (Collections.frequency(map.values(), 3) == 2)
        //     || (map.containsValue(3) && map.containsValue(2));
    }

    public boolean testFirstQuinte(List<Card> cards) {
        return cards.getLast().getCardValue().equals(CardValue.AS)
            && cards.getFirst().getCardValue().equals(CardValue.DEUX)
            && cards.get(1).getCardValue().equals(CardValue.TROIS)
            && cards.get(2).getCardValue().equals(CardValue.QUATRE)
            && cards.get(3).getCardValue().equals(CardValue.CINQ);
    }

    public List<Card> isQuinte(HandData hand) {
        // Enlever les doublons
        LinkedHashSet<Card> set = new LinkedHashSet<>(hand.getSortedCards());
        List<Card> cards = new ArrayList<>();
        cards.addAll(set);

        int count = 0;

        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getCardValue().getValue() == 
                (cards.get(i + 1).getCardValue().getValue() + 1)) {
                 count++;   
            } else {
                count = 0;
            }
        }

        if (count >= 5) {
            return best;
        } else {
            return testFirstQuinte(cards);
        }
    }

    public boolean isBrelan(HandData hand) {
        return hand.getCountCardValues().containsValue(3);
    }

    public boolean isDoublePaire(HandData hand) {
        return Collections.frequency(
            hand.getCountCardValues().values(), 2
        ) >= 2;
    }
    
    public boolean isPaire(HandData hand) {
        return Collections.frequency(
            hand.getCountCardValues().values(), 2
        ) == 1;    
    }

    public boolean isFlush(HandData hand) {
        return hand.getCountCardColors().containsValue(5);
    }
    
    public boolean isQuinteFlush(HandData hand) {
        return isQuinte(hand) && isFlush(hand);
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
        
        hand.setType(HandType.CARTE_HAUTE); 
        return;
    }

    public HandData evaluateSinglePlayer(PlayerSession player, List<Card> community) {
        List<Card> sortedCards = sortCards(player, community);
        HashMap<CardValue, Integer> cardValues = countCardValues(sortedCards);
        HashMap<CardColor, Integer> cardColors = countCardColors(sortedCards);

        HandData hand = new HandData(cardValues, cardColors, sortedCards);
        evaluateHand(hand);
        
        return hand;
    }

    public int compareTieBreak(HandData h1, HandData h2) {
        List<Card> h1List = h1.getSortedCards();
        List<Card> h2List = h2.getSortedCards();

        // A corriger: prendre en compte le kicker, ...
        for (int i = 0; i < h1List.size(); i++) {
            int v1 = h1List.get(i).getCardValue().getValue();
            int v2 = h2List.get(i).getCardValue().getValue();

            if (v1 < v2) {
                return -1;
            } else if (v1 > v2) {
                return 1;
            } else {
                continue;
            }
        }

        return 0;
    }

    public int compareHands(HandData h1, HandData h2) {
        int h1Score = h1.getType().getScore();
        int h2Score = h2.getType().getScore();
        if (h1Score < h2Score) {
            return -1;
        } else if (h1Score > h2Score) {
            return 1;
        } else {
            return compareTieBreak(h1, h2);
        }
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
