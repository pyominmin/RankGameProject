package rank.game.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PubgApiService {

    // application에 저장된 API 가져오기
    @Value("${pubg.api.key}")
    private String apiKey;

    private final String BASE_URL = "https://api.pubg.com/shards/";

    public String getPlayerStatsByPlatform(String platform, String playerName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String platformUrl = BASE_URL + platform + "/players?filter[playerNames]=" + playerName;
        ResponseEntity<String> playerResponse = restTemplate.exchange(platformUrl, HttpMethod.GET, entity, String.class);

        if (playerResponse.getStatusCodeValue() == 200) {
            String playerId = extractPlayerId(playerResponse.getBody());

            String statsUrl = BASE_URL + platform + "/players/" + playerId + "/seasons/lifetime";
            ResponseEntity<String> statsResponse = restTemplate.exchange(statsUrl, HttpMethod.GET, entity, String.class);

            if (statsResponse.getStatusCodeValue() == 200) {
                return statsResponse.getBody();
            }
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "플레이어 정보를 찾을 수 없습니다.");
    }

    private String extractPlayerId(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data").get(0);
            return dataNode.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("플레이어 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }
}
