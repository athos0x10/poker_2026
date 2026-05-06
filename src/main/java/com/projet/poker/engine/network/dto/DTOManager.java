package com.projet.poker.engine.network.dto;
import java.util.List;

public class DTOManager {

    // ================ SERVER -> CLIENT ================

    // --- DTOs PRIVES ---
    public record HandCardsDTO(String type, List<String> cards_ids) {}

    public record ActionAckDTO(String type, int i, String detail) {}

    public record PlayerTurnDTO(String type) {}

    public record BestComboDTO(String type, String rank, List<String> cards_ids) {}

    // --- DTOs BROADCAST ---
    public record PotUpdateDTO(String type, double amount) {}

    public record BoardUpdateDTO(String type, List<String> new_cards) {}

    public record UserQuitDTO(String type, long user_id) {}

    public record BetStackUpdateDTO(String type, long user_id, double currentBet, double currentStack) {}

    // --- DTOs COMPLEXES (Sync & Showdown) ---
    public record PlayerInfoDTO(long id, double stack, double bet, boolean folded, boolean allIn) {}

    public record FullGameInfosDTO(String type, String tableName, String gameState, double pot, 
                                List<String> board, double highestBet, int currentTurn, 
                                List<PlayerInfoDTO> players) {}

    public record WinnerInfoDTO(long id, String hand, List<String> holeCards) {}

    public record ShowdownDTO(String type, List<String> finalBoard, List<WinnerInfoDTO> gameWinners) {}

    // ================ CLIENT -> SERVER ================

    public record ActionRequestDTO(long playerId, String action, double amount) {}

}
