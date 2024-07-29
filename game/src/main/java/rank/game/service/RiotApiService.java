package rank.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rank.game.dto.MatchDTO;
import rank.game.dto.PlayerDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class RiotApiService {
    private final RestTemplate restTemplate;
    private final String apiKey = "RGAPI-ef59b225-02cc-490a-8945-33539eb516f0";
    private final String headerName = "X-Riot-Token";

    public RiotApiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getPuuid(String gameName, String tagLine) {
        String url = String.format("https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s", gameName, tagLine, apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (String) response.getBody().get("puuid");
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return getPuuid(gameName, tagLine); // 재귀 호출
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
        }
        return null;
    }

    public String getSummonerIdByPuuid(String puuid) {
        String url = String.format("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s", puuid, apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (String) response.getBody().get("id");
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return getSummonerIdByPuuid(puuid); // 재귀 호출
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
        }
        return null;
    }

    public PlayerDTO getSortedPlayers(String gameName, String tagLine) {
        String puuid = getPuuid(gameName, tagLine);
        if (puuid != null) {
            String summonerId = getSummonerIdByPuuid(puuid);
            String tier = getLEAGUEV4(summonerId);
            PlayerDTO playerInfo = new PlayerDTO(puuid, gameName, tagLine, tier);
            log.info("gameName: {}, tagLine: {}, puuid: {}, summonerId: {}, tier: {}", gameName, tagLine, puuid, summonerId, tier);
            try {
                Thread.sleep(1200); // 요청 간 대기 시간
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return playerInfo;
        }

        return null;
    }

    public String getLEAGUEV4(String summonerId) {
        String url = String.format("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/%s?api_key=%s", summonerId, apiKey);

        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            log.info("Response from LEAGUEV4 API: {}", (Object) response.getBody()); // 응답 데이터 로그 출력
            if (response.getBody() != null && response.getBody().length > 0) {
                for (Map<String, Object> entry : response.getBody()) {
                    if ("RANKED_SOLO_5x5".equals(entry.get("queueType"))) {
                        return (String) entry.get("tier");
                    }
                }
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
                return getLEAGUEV4(summonerId); // 재시도
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
        }
        return null;
    }

    public List<MatchDTO> getMatchesByPuuid(String puuid) {
        String url = String.format("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=10&api_key=%s", puuid, apiKey);
        log.info("매치 ID 목록을 가져오기 위한 요청 URL: {}", url);
        try {
            ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
            String[] matchIds = response.getBody();
            log.info("API 응답에서 가져온 매치 ID 목록: {}", Arrays.toString(matchIds));

            List<MatchDTO> matches = new ArrayList<>();
            for (String matchId : matchIds) {
                log.info("매치 세부 정보를 가져오기 위한 매치 ID: {}", matchId);
                MatchDTO match = getMatchDetails(matchId);
                if (match != null) {
                    matches.add(match);
                    log.info("매치 세부 정보를 성공적으로 가져왔습니다: {}", match);
                } else {
                    log.warn("매치 세부 정보를 가져오지 못했습니다. 매치 ID: {}", matchId);
                }
            }
            log.info("총 매치 수: {}", matches.size());
            return matches;
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private MatchDTO getMatchDetails(String matchId) {
        String url = String.format("https://asia.api.riotgames.com/lol/match/v5/matches/%s?api_key=%s", matchId, apiKey);
        log.info("매치 세부 정보를 가져오기 위한 요청 URL: {}", url);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> details = response.getBody();
            HttpHeaders headers = response.getHeaders();
            String dateHeader = headers.getFirst("Date");
            log.info("응답 헤더의 Date: {}", dateHeader);
            log.info("API 응답: {}", details);

            if (details == null || !details.containsKey("info")) {
                log.error("응답 데이터에 'info' 키가 없습니다.");
                return null;
            }

            Map<String, Object> info = (Map<String, Object>) details.get("info");

            // 게임 모드, 생성 시간, 지속 시간
            String gameType = (String) info.get("gameType");
            Long gameCreation = info.containsKey("gameCreation") ? ((Number) info.get("gameCreation")).longValue() : null;
            log.info("게임 모드: {}, 게임 생성 시간: {}", gameType, gameCreation);

            // 게임 시작 시간
            Long gameStartTimestamp = info.containsKey("gameStartTimestamp") ? ((Number) info.get("gameStartTimestamp")).longValue() : null;

            // 플레이어 정보 추출
            List<Map<String, Object>> participants = (List<Map<String, Object>>) info.get("participants");
            if (participants == null || participants.isEmpty()) {
                log.error("참가자 목록이 비어 있습니다.");
                return null;
            }

            // 첫 번째 참가자만을 예시로 사용
            Map<String, Object> participant = participants.get(0);
            log.info("참가자 정보: {}", participant);

            Long timePlayed = participant.containsKey("timePlayed") ? ((Number) participant.get("timePlayed")).longValue() : null;
            Boolean win = (Boolean) participant.get("win");
            String championName = (String) participant.get("championName");
            Integer champLevel = (Integer) participant.get("champLevel");
            int kills = participant.containsKey("kills") ? ((Number) participant.get("kills")).intValue() : 0;
            int deaths = participant.containsKey("deaths") ? ((Number) participant.get("deaths")).intValue() : 0;
            int assists = participant.containsKey("assists") ? ((Number) participant.get("assists")).intValue() : 0;
            double kda = deaths != 0 ? (kills + assists) / (double) deaths : kills + assists;
            int visionWardsBought = participant.containsKey("visionWardsBoughtInGame") ? ((Number) participant.get("visionWardsBoughtInGame")).intValue() : 0;
            int totalMinionsKilled = participant.containsKey("totalMinionsKilled") ? ((Number) participant.get("totalMinionsKilled")).intValue() : 0;
            double csPerMinute = timePlayed != null && timePlayed != 0 ? (totalMinionsKilled / (timePlayed / 60.0)) : 0;
            log.info("챔피언 이름: {}, 챔피언 레벨: {}, 킬: {}, 데스: {}, 어시스트: {}, KDA: {}, 구매한 제어 와드 수: {}, 총 미니언 처치 수: {}, 분당 CS: {}",
                    championName, champLevel, kills, deaths, assists, kda, visionWardsBought, totalMinionsKilled, csPerMinute);

            // 승리 또는 패배
            String result = (win != null && win) ? "승리" : "패배";
            log.info("경기 결과: {}", result);

            return new MatchDTO(
                    win != null && win,
                    matchId,
                    gameType,
                    gameCreation,
                    timePlayed,
                    championName != null ? championName : "챔피언 없음",
                    champLevel != null ? champLevel : 0,
                    kills,
                    deaths,
                    assists,
                    kda,
                    visionWardsBought,
                    totalMinionsKilled,
                    csPerMinute,
                    gameStartTimestamp != null ? gameStartTimestamp : 0L,
                    dateHeader  // Date 헤더 추가
            );
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}