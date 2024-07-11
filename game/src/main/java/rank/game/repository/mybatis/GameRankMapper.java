package rank.game.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import rank.game.dto.GameHistoryDTO;

import java.util.List;

@Mapper
public interface GameRankMapper {
    List<GameHistoryDTO> getRankedGames();
}
