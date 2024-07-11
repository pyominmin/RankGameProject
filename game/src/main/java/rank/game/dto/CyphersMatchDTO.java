package rank.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CyphersMatchDTO {
    private String matchId;
    private String result;
    private String characterName;
    private String playTime;
    private String characterId; // 캐릭터 이미지
    private String level;
    private String kda;
    private String kills;
    private String deaths;
    private String assists;
}
