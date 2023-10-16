package rhetorike.glot.domain._1auth.service.orgnamesearch.academy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import rhetorike.glot.domain._1auth.service.orgnamesearch.academy.AcademySearchStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class NicePortalSearch implements AcademySearchStrategy {
    private final static String KEY = "c73a22ada0584131a3f5b058e80db845";
    private final static String HTTPS = "https";
    private final static String HOST = "open.neis.go.kr";
    private final static String LOCATION_SEARCH_API = "/hub/acaInsTiInfo";
    private static final String[] REGION_CODES = {"B10", "C10", "D10", "E10", "F10", "G10", "H10", "I10", "J10", "K10", "M10", "N10", "P10", "Q10", "R10", "S10", "T10"};

    @Override
    public List<String> search(String keyword) {
        List<String> result = new ArrayList<>();
        WebClient wc = createWebClient();
        for (String code : REGION_CODES) {
            result.addAll(getResult(wc, keyword, code));
        }
        return result;
    }

    private WebClient createWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("localhost:8080");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl("localhost:8080")
                .build();
    }

    private List<String> getResult(WebClient wc, String keyword, String code) {
        String str = wc.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(LOCATION_SEARCH_API)
                        .queryParam("KEY", KEY)
                        .queryParam("Type", "json")
                        .queryParam("ATPT_OFCDC_SC_CODE", code)
                        .queryParam("ACA_NM", keyword)
                        .queryParam("pSize", "10")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            YourDataClass searchResult = new ObjectMapper().readValue(str, YourDataClass.class);
            if (searchResult.getAcaInsTiInfo() == null) {
                return Collections.emptyList();
            }
            return searchResult.getAcaInsTiInfo().stream()
                    .filter(c -> c.getRow() != null)
                    .flatMap(c -> c.getRow().stream())
                    .map(Row::getAcaNm)
                    .toList();
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class YourDataClass {
        @JsonProperty("acaInsTiInfo")
        private List<AcaInsTiInfo> acaInsTiInfo = null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class AcaInsTiInfo {
        @JsonProperty("row")
        private List<Row> row = null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class Row {
        @JsonProperty("ACA_NM")
        private String acaNm;
    }
}