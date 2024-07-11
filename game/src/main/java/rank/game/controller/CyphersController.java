package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import rank.game.dto.CyphersMatchDTO;
import rank.game.dto.CyphersPlayerDTO;
import rank.game.service.CyphersApiService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/search/cyphers")
public class CyphersController {

    private final CyphersApiService cyphersApiService;

    @Autowired
    public CyphersController(CyphersApiService cyphersApiService) {
        this.cyphersApiService = cyphersApiService;
    }

    @GetMapping
    public String cyphers(HttpSession session, Model model) {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        return "html/cyphers";
    }

    @PostMapping("/sorted-players")
    public String getSortedPlayers(@RequestParam String playerName,
                                   @RequestParam(defaultValue = "full") String wordType,
                                   Model model, HttpSession session) {
        try {
            CyphersPlayerDTO playerInfo = cyphersApiService.getPlayerInfo(playerName, wordType);
            List<CyphersMatchDTO> matchDetails = cyphersApiService.getMatchDetails(playerInfo.getPlayerId());

            model.addAttribute("playerInfo", playerInfo);
            model.addAttribute("matchDetails", matchDetails);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "플레이어 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "플레이어 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        return "html/cyphers";
    }
}
