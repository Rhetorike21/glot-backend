package rhetorike.glot.domain._1auth.service.orgnamesearch.school;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import rhetorike.glot.domain._1auth.service.orgnamesearch.academy.NicePortalSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class CareerNetSearch implements SchoolSearchStrategy {
    private final static String KEY = "002989df39ca0ca23731689de0198566";
    private final static String HTTPS = "https";
    private final static String HOST = "www.career.go.kr";
    private final static String LOCATION_SEARCH_API = "/cnet/openapi/getOpenApi.json?apiKey=인증키";
    private static final String[] SCHOOL_CODE = {"elem_list", "midd_list", "high_list", "univ_list", "seet_list"};

    @Override
    public List<String> search(String keyword) {
        List<String> result = new ArrayList<>();
        WebClient wc = createWebClient();
        for (String code : SCHOOL_CODE) {
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
                        .queryParam("apiKey", KEY)
                        .queryParam("svcType", "api")
                        .queryParam("svcCode", "SCHOOL")
                        .queryParam("contentType", "json")
                        .queryParam("gubun", code)
                        .queryParam("searchSchulNm", keyword)
                        .queryParam("perPage", "20")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            DataSearch dataSearch = new ObjectMapper().readValue(str, YourDataClass.class).getDataSearch();
            if (dataSearch.getContent() == null) {
                return Collections.emptyList();
            }
            return dataSearch.getContent().stream()
                    .map(Content::getSchoolName)
                    .toList();
        } catch (JsonProcessingException ex) {
            return Collections.emptyList();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class YourDataClass {
        @JsonProperty("dataSearch")
        private DataSearch dataSearch;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class DataSearch {
        @JsonProperty("content")
        List<Content> content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class Content {
        @JsonProperty("schoolName")
        String schoolName;
    }
}
