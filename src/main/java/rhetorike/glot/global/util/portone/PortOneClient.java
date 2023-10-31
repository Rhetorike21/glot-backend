package rhetorike.glot.global.util.portone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import rhetorike.glot.global.error.exception.ConnectionFailedException;

import java.util.UUID;

@Slf4j
@Component
public class PortOneClient {

    private final static String HTTPS = "https";
    private final static String HOST = "api.iamport.kr";
    private final static String TOKEN_URI = "/users/getToken";
    private final static String ONETIME_PAY_URI = "/subscribe/payments/onetime";
    private final static String HISTORY_URI = "/payments/{imp_uid}";
    private final static String AGAIN_URI = "/subscribe/payments/again";
    @Value("${api.port-one.imp-key}")
    private String IMP_KEY;
    @Value("${api.port-one.imp-secret}")
    private String IMP_SECRET;


    /**
     * 액세스 토큰 발급
     *
     * @return 액세스 토큰
     */
    public TokenResponse getAccessToken() {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("imp_key", IMP_KEY);
        param.add("imp_secret", IMP_SECRET);

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(TOKEN_URI)
                        .build()
                )
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Response response = getData(result, TokenResponseForm.class);
        return (TokenResponse) response;
    }

    public OneTimePayResponse payAndSaveBillingKey(String merchantUid) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("merchant_uid", merchantUid); // 매번 다른 코드를 넘겨줘야 함
        param.add("amount", "1000");
        param.add("card_number", "5462-1234-0920-1234"); //테스트 시 카드 정보 상관 없음
        param.add("expiry", "2026-11");
        param.add("birth", "000123");
        param.add("pg", "tosspayments"); //나이스페이먼츠 -> nice_v2

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(ONETIME_PAY_URI)
                        .build()
                )
                .header("Authorization", "Bearer " + getAccessToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Response response = getData(result, OneTimePayResponseForm.class);
        return (OneTimePayResponse) response;
    }

    public HistoryResponse getPaymentsHistory(String impUid) {
        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(HISTORY_URI)
                        .build(impUid)
                )
                .header("Authorization", "Bearer " + getAccessToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Response response = getData(result, HistoryResponseForm.class);
        return (HistoryResponse) response;
    }

    public AgainResponse payAgain(String customerUid, String merchantUid) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("customer_uid", customerUid);
        param.add("merchant_uid", merchantUid);
        param.add("amount", "1000");
        param.add("name", "사과");

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(AGAIN_URI)
                        .build()
                )
                .header("Authorization", "Bearer " + getAccessToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Response response = getData(result, AgainResponseForm.class);
        return (AgainResponse) response;
    }

    private <T extends ResponseForm> Response getData(String result, Class<T> form) {
        T responseForm;
        try {
            responseForm = new ObjectMapper().readValue(result, form);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (responseForm.getCode() == 0) {
            return responseForm.getResponse();
        }
        log.error(responseForm.getMessage());
        throw new ConnectionFailedException();
    }

    private WebClient createWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("localhost:8080");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        return WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl("localhost:8080")
                .build();
    }

    @ToString
    @Getter
    public static abstract class ResponseForm {
        private int code;
        private String message;
        private Response response;
    }

    @ToString
    @Getter
    public static class TokenResponseForm extends ResponseForm {
        private TokenResponse response;
    }

    @ToString
    @Getter
    public static class OneTimePayResponseForm extends ResponseForm {
        private OneTimePayResponse response;
    }

    @ToString
    @Getter
    public static class HistoryResponseForm extends ResponseForm {
        private HistoryResponse response;
    }

    @ToString
    @Getter
    public static class AgainResponseForm extends ResponseForm {
        private AgainResponse response;
    }

    public static abstract class Response {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class TokenResponse extends Response{
        @JsonProperty("access_token")
        private String accessToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class OneTimePayResponse extends Response{
        @JsonProperty("imp_uid")
        private String impUid;
        private String pay_method;
        private String bank_code;
        private String bank_name;
        private String buyer_name;
        private String client_uid;
        private String customer_uid;
        private String status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class HistoryResponse extends Response{
        @JsonProperty("imp_uid")
        private String impUid;
        private String pay_method;
        private String bank_code;
        private String bank_name;
        private String buyer_name;
        private String client_uid;
        private String status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class AgainResponse extends Response{
        @JsonProperty("imp_uid")
        private String impUid;
        private String pay_method;
        private String bank_code;
        private String bank_name;
        private String buyer_name;
        private String client_uid;
        private String status;
    }
}
