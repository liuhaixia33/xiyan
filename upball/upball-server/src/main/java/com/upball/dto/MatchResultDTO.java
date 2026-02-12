package com.upball.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchResultDTO {
    
    private Integer homeScore;
    private Integer awayScore;
    private List<PlayerStatsDTO> playerStats;
    
    @Data
    public static class PlayerStatsDTO {
        private Long userId;
        private Integer goals;
        private Integer assists;
        private Integer yellowCards;
        private Integer redCards;
        private Double rating;
    }
}
