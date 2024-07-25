package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.VoteDTO;
import rank.game.entity.Game;
import rank.game.entity.VoteEntity;
import rank.game.repository.GameRepository;
import rank.game.repository.VoteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private VoteRepository voteRepository;

    public List<VoteDTO> findAllVoteDTOs() {
        List<Game> games = gameRepository.findAll();
        return games.stream()
                .map(game -> new VoteDTO(game.getGameName(), game.getImageUrl()))
                .collect(Collectors.toList());
    }

    public void incrementVote(String gameName) {
        Optional<Game> gameOptional = gameRepository.findByGameName(gameName);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            game.setGameVote(game.getGameVote() + 1);
            gameRepository.save(game);
        } else {
            throw new RuntimeException("Game not found");
        }
    }

    public void saveVotes(String nickname, List<String> gameNames) {
        for (String gameName : gameNames) {
            VoteEntity vote = new VoteEntity();
            vote.setNickname(nickname);
            vote.setGameName(gameName);
            vote.setVoteTime(LocalDate.now());
            voteRepository.save(vote);

            incrementVote(gameName);
        }
    }

    public boolean hasVotedToday(String nickname) {
        LocalDate today = LocalDate.now();
        return voteRepository.existsByNicknameAndVoteTime(nickname, today);
    }

    public List<VoteEntity> getVotes() {
        return voteRepository.findAll();
    }
}
