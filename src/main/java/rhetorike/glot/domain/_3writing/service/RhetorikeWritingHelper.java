package rhetorike.glot.domain._3writing.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;
import java.util.List;

@Primary
@Slf4j
@Service
public class RhetorikeWritingHelper implements WritingHelper{

    private final static String HTTPS = "http";
    private final static String HOST = "13.51.73.116";
    private final static String URI = "/{conj}/{sent}";

    @Override
    public List<String> help(Type type, String sentence) {
        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(URI)
                        .build(type.param(), sentence)
                )
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(2))
                .block();
        log.info(result);
        try {
            ResponseForm responseForm = new ObjectMapper().readValue(result, ResponseForm.class);
            return responseForm.getResponse();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseForm{
        List<String> response;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response{
        List<String> response;
    }

    private WebClient createWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("localhost:8080");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl("localhost:8080")
                .build();
    }
}
