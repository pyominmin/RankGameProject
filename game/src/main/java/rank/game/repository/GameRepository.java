package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rank.game.entity.Game;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameName(String gameName);
}
