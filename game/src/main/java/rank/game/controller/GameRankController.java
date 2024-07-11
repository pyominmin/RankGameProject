package rank.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import rank.game.entity.Game;
import rank.game.entity.GameHistory;
import rank.game.service.GameRankService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
//@RequestMapping("/rank")
public class GameRankController {
    private static final Logger log = LoggerFactory.getLogger(GameRankController.class);
    @Autowired
    private GameRankService gameService;
    @Autowired
    private ObjectMapper objectMapper;

    // 일간 top3 게임, 인기 게임 순위 처리
    @GetMapping("/review")
    public String getReviewPage(Model model, HttpSession session) throws JsonProcessingException {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        List<GameHistory> top10Games = gameService.getWeeklyTop10Games(LocalDateTime.now().minusDays(7), LocalDateTime.now());
        List<Game> games = gameService.getAllGames();

        String top10GamesJson = objectMapper.writeValueAsString(top10Games);

        model.addAttribute("top3GamesJson", top10GamesJson);
        model.addAttribute("games", games);

        return "html/review";
    }

    // 일별 게임 순위 변동현황 처리
    @GetMapping("/game/{id}/vote-trend")
    @ResponseBody
    public List<GameHistory> getVoteTrend(@PathVariable Long id) {

        return gameService.getVoteTrend(id);

    }
    
    // Top 10 게임 처리
    @GetMapping("/api/games/top10")
    public ResponseEntity<List<GameHistory>> getTop10Games() {
        LocalDateTime start = LocalDateTime.now().minusDays(7); // 최근 7일 데이터 조회
        LocalDateTime end = LocalDateTime.now();

        List<GameHistory> top10Games = gameService.getWeeklyTop10Games(start, end);
        return ResponseEntity.ok(top10Games);
    }

}
