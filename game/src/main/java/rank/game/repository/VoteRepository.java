package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rank.game.entity.VoteEntity;

import java.time.LocalDate;
import java.util.List;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    @Query("SELECT v.gameName FROM VoteEntity v WHERE v.nickname = :nickname")
    List<String> findGameNamesByNickname(@Param("nickname") String nickname);

    boolean existsByNicknameAndVoteTime(String nickname, LocalDate voteTime);
}
