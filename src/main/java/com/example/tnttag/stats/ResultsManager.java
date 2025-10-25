package com.example.tnttag.stats;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates and displays end-game results
 */
public class ResultsManager {
    
    private final TNTTagPlugin plugin;
    
    public ResultsManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Generate and display results to all players
     */
    public void displayResults(GameInstance game, Player winner) {
        List<String> results = generateResults(game, winner);
        
        // Send to all players who were in the game
        for (Player player : game.getPlayers()) {
            for (String line : results) {
                player.sendMessage(line);
            }
        }
    }
    
    /**
     * Generate results as a list of strings
     */
    public List<String> generateResults(GameInstance game, Player winner) {
        List<String> results = new ArrayList<>();
        
        // Header
        results.add("§e§l========== GAME RESULT ==========");
        results.add("§6§l        🏆 最終結果 🏆");
        results.add("");
        
        // Rank players by rounds survived (descending = better)
        List<PlayerResult> rankings = rankPlayers(game);
        
        // Top 3
        int rank = 1;
        for (int i = 0; i < Math.min(3, rankings.size()); i++) {
            PlayerResult pr = rankings.get(i);
            String medal = getMedal(rank);
            String achievement = getAchievement(pr.roundsEliminated, game.getCurrentRound());
            
            results.add(String.format("§e%s %d位: §f%s §7- %s", 
                medal, rank, pr.playerName, achievement));
            rank++;
        }
        
        results.add("");
        
        // Survival breakdown
        results.add("§e生存ラウンド数:");
        Map<Integer, Integer> breakdown = getSurvivalBreakdown(game);
        
        for (int round = 6; round >= 1; round--) {
            int count = breakdown.getOrDefault(round, 0);
            if (count > 0) {
                String roundText = round == 6 ? "ラウンド6完走" : "ラウンド" + round + "脱落";
                results.add(String.format("  §7%s: §f%d人", roundText, count));
            }
        }
        
        results.add("");
        results.add(String.format("§7総参加者: §f%d人", game.getPlayers().size()));
        results.add("§e§l==================================");
        
        return results;
    }
    
    /**
     * Rank players by performance
     */
    private List<PlayerResult> rankPlayers(GameInstance game) {
        List<PlayerResult> results = new ArrayList<>();
        
        for (Player player : game.getPlayers()) {
            PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
            
            int roundsEliminated = data.getRoundEliminated();
            if (roundsEliminated == -1) {
                roundsEliminated = 7; // Survived all 6 rounds
            }
            
            results.add(new PlayerResult(
                player.getName(),
                roundsEliminated,
                data.getTntTagsGiven()
            ));
        }
        
        // Sort by rounds eliminated (higher = better), then by tags given (higher = better)
        results.sort((a, b) -> {
            int roundCompare = Integer.compare(b.roundsEliminated, a.roundsEliminated);
            if (roundCompare != 0) {
                return roundCompare;
            }
            return Integer.compare(b.tagsGiven, a.tagsGiven);
        });
        
        return results;
    }
    
    /**
     * Get survival breakdown by round
     */
    private Map<Integer, Integer> getSurvivalBreakdown(GameInstance game) {
        Map<Integer, Integer> breakdown = new HashMap<>();
        
        for (Player player : game.getPlayers()) {
            PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
            int roundEliminated = data.getRoundEliminated();
            
            if (roundEliminated == -1) {
                // Survived all rounds
                breakdown.put(6, breakdown.getOrDefault(6, 0) + 1);
            } else {
                breakdown.put(roundEliminated, breakdown.getOrDefault(roundEliminated, 0) + 1);
            }
        }
        
        return breakdown;
    }
    
    /**
     * Get medal emoji for rank
     */
    private String getMedal(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "  ";
        }
    }
    
    /**
     * Get achievement text based on elimination round
     */
    private String getAchievement(int roundEliminated, int totalRounds) {
        if (roundEliminated == 7 || roundEliminated == -1) {
            return "§a全ラウンド生存！";
        } else if (roundEliminated == 6) {
            return "§eラウンド6まで生存";
        } else if (roundEliminated == 5) {
            return "§eラウンド5まで生存";
        } else {
            return "§7ラウンド" + roundEliminated + "まで生存";
        }
    }
    
    /**
     * Helper class for player results
     */
    private static class PlayerResult {
        String playerName;
        int roundsEliminated;
        int tagsGiven;
        
        PlayerResult(String playerName, int roundsEliminated, int tagsGiven) {
            this.playerName = playerName;
            this.roundsEliminated = roundsEliminated;
            this.tagsGiven = tagsGiven;
        }
    }
}
