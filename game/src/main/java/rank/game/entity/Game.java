package rank.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "game_vote", nullable = false)
    private int gameVote;

    @Column(name = "game_rank", nullable = false)
    private Integer gameRank;

    @Column(name = "game_image", nullable = false)
    private String imageUrl;

}
