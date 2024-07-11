package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.GameHistoryDTO;
import rank.game.entity.Game;
import rank.game.entity.GameHistory;
import rank.game.repository.GameHistoryRepository;
import rank.game.repository.GameRepository;
import rank.game.repository.mybatis.GameRankMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameRankService {
    @Autowired
    private GameHistoryRepository gameHistoryRepository;
    @Autowired
    private GameRepository gameRepository;
    //
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    //
    public List<GameHistory> getVoteTrend(Long gameId) { return gameHistoryRepository.findByGameIdOrderByVoteTimeAsc(gameId); }

    // TOP 10
    public List<GameHistory> getWeeklyTop10Games(LocalDateTime start, LocalDateTime end) { return gameHistoryRepository.findTop10ByVoteTimeBetweenOrderByGameVoteDesc(start, end); }


}


