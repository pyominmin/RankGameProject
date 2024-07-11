package rank.game.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameHistoryDTO {
    private Long id;
    private Long gameId;
    private String gameName;
    private Integer gameVote;
    private Integer gameRank;
    private LocalDateTime voteTime;
    private Integer ranking;
    private Integer totalVotes;

    // 기본 생성자
    public GameHistoryDTO() {
    }

    // 모든 필드를 포함한 생성자
    public GameHistoryDTO(Long id, Long gameId, String gameName, Integer gameVote, Integer gameRank, LocalDateTime voteTime, Integer ranking, Integer totalVotes) {
        this.id = id;
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameVote = gameVote;
        this.gameRank = gameRank;
        this.voteTime = voteTime;
        this.ranking = ranking;
        this.totalVotes = totalVotes;
    }

}
