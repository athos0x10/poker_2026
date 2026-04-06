package main.java.com.projet.poker.engine.logger;

import main.java.com.projet.poker.engine.Card;
import main.java.com.projet.poker.model.game.GameHand;
import main.java.com.projet.poker.model.game.PlayerSession;
import main.java.com.projet.poker.model.game.Table;

public class ConsoleGameLogger implements GameLogger {

    @Override
    public void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void logError(String error) {
        System.err.println("[ERROR] " + error);
    }

    @Override
    public void logAllInfos(Table table) {
        System.out.println( "--------------- TABLE --------------");
        logTableInfos(table);
        System.out.println( "--------------- GAME HAND --------------");
        logGameHandInfos(table);
        System.out.println( "--------------- CURRENT PLAYER --------------");
        logCurrentPlayerInfos(table);
    }

    @Override
    public void logTableInfos(Table table) {
        System.out.println("Table: " + table.getName());
        System.out.println("Min Bet: " + table.getMinBet());
        System.out.println("Max Players: " + table.getMaxPlayers());
        System.out.println("Current Players: " + table.getActivePlayers().size());
        System.out.println("Game State: " + table.getGameState());
    }

    @Override
    public void logGameHandInfos(Table table) {
        GameHand gameHand = table.getGameHand();
        System.out.println("Current Pot: " + gameHand.getPotAmount());
        System.out.println("Community Cards: " + (gameHand.getCommunityCards().size() > 0 ? gameHand.getCommunityCards() : "None"));
        System.out.println("Dealer Button: Player " + (gameHand.getDealerButton() != null ? gameHand.getDealerButton().getId() : "None"));
        System.out.println("Highest Bet: " + gameHand.getHighestBet());
        System.out.println("Small Blind: " + gameHand.getSmallBlindAmount() + " (Player " + (gameHand.getSmallBlindPlayer() != null ? gameHand.getSmallBlindPlayer().getId() : "None") + ")");
        System.out.println("Big Blind: " + gameHand.getBigBlindAmount() + " (Player " + (gameHand.getBigBlindPlayer() != null ? gameHand.getBigBlindPlayer().getId() : "None") + ")");
        System.out.println("Current Turn: Player " + table.getActivePlayers().get(gameHand.getCurrentTurnIndex()).getId());
    }
    
    @Override
    public void logPlayerInfo(PlayerSession player) {
        System.out.println("Player ID: " + player.getId());
        System.out.println("Seat Number: " + player.getSeatNumber());
        System.out.println("Current Stack: " + player.getCurrentStack());
        System.out.println("Has Folded: " + player.hasFolded());
        System.out.println("Is All-In: " + player.isAllIn());
        System.out.println("Bet in Current Round: " + player.getBetInCurrentRound());
    }

    @Override
    public void logCurrentPlayerInfos(Table table) {
        GameHand gameHand = table.getGameHand();
        PlayerSession currentPlayer = table.getActivePlayers().get(gameHand.getCurrentTurnIndex());
        logPlayerInfo(currentPlayer);
    }

    @Override
    public void logPlayerHands(Table table) {
        for (PlayerSession player : table.getActivePlayers()) {
            System.out.println("Player " + player.getId() + ": ");
            logPlayerHoleCards(player);
        }
    }

    @Override
    public void logWinners(Table table) {
        System.out.println("Winners:");
        for (PlayerSession winner : table.getWinners()) {
            System.out.println("Player " + winner.getId() + " with hand: ");
            logPlayerHoleCards(winner);
        }
    }

    @Override
    public void logCommunityCards(Table table) {
        System.out.print("Community Cards: ");

        for (Card c : table.getGameHand().getCommunityCards()) {
            System.out.print(c.toString() + " ");
        }
        System.out.println();
    }


    @Override
    public void logPlayerHoleCards(PlayerSession player) {
        for (Card c : player.getHoleCards()) {
            System.out.print(c.toString() + " ");
        }
        System.out.println();
    }

    @Override
    public void logPotAmount(Table table) {
        System.out.println("Current Pot: " + table.getGameHand().getPotAmount());
    }

    @Override
    public void logHighestBet(Table table) {
        System.out.println("Current Highest Bet: " + table.getGameHand().getHighestBet());
    }

    @Override
    public void logPlayerStack(PlayerSession player) {
        System.out.println("Player " + player.getId() + " Stack: " + player.getCurrentStack());
    }

    @Override
    public void logPlayerBet(PlayerSession player) {
        System.out.println("Player " + player.getId() + " bets: " + player.getBetInCurrentRound());
    }

    @Override
    public void logPlayerAction(PlayerSession player, String actionType, double amount) {
        System.out.println("Player " + player.getId() + " performs action: " + actionType + (amount > 0 ? " with amount: " + amount : ""));
        
        System.out.println("Player " + player.getId() + " Stack after action: " + player.getCurrentStack());
    }
}
