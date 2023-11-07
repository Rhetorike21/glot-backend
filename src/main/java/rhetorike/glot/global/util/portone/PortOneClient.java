package rhetorike.glot.global.util.portone;

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
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.ConnectionFailedException;

@Slf4j
@Component
public class PortOneClient {

    private final static String HTTPS = "https";
    private final static String HOST = "api.iamport.kr";
    private final static String TOKEN_URI = "/users/getToken";
    private final static String BILLING_KEY_URI = "/subscribe/customers/{customer_uid}";
    private final static String ONETIME_PAY_URI = "/subscribe/payments/onetime";
    private final static String HISTORY_URI = "/payments/{imp_uid}";
    private final static String AGAIN_URI = "/subscribe/payments/again";
    private final static String CANCEL_URI = "/payments/cancel";
    private final static String DELETE_BILLING_KEY = "/subscribe/customers/{customer_uid}";
    @Value("${api.port-one.imp-key}")
    private String IMP_KEY;
    @Value("${api.port-one.imp-secret}")
    private String IMP_SECRET;


    /**
     * 액세스 토큰 발급
     *
     * @return 액세스 토큰
     */
    public PortOneResponse.Token issueToken() {
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
        PortOneResponse response = getData(result, PortOneForm.Token.class);
        return (PortOneResponse.Token) response;
    }

    public PortOneResponse.OneTimePay payAndSaveBillingKey(Order order, Payment payment) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("merchant_uid", String.valueOf(order.getId()));
        param.add("customer_uid", String.valueOf(order.getUser().getId()));
        param.add("name", order.getPlan().getName());
        param.add("amount", String.valueOf(order.totalAmount()));
        param.add("card_number", payment.getCardNumber()); //테스트 시 카드 정보 상관 없음
        param.add("expiry", payment.getExpiry());
        param.add("birth", payment.getBirthDate());
        param.add("pwd_2digit", payment.getPassword());

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(ONETIME_PAY_URI)
                        .build()
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.OneTimePay.class);
        return (PortOneResponse.OneTimePay) response;
    }

    public PortOneResponse.PayHistory getPaymentsHistory(String impUid) {
        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(HISTORY_URI)
                        .build(impUid)
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.History.class);
        return (PortOneResponse.PayHistory) response;
    }

    public PortOneResponse.AgainPay payAgain(Order order) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("merchant_uid", order.getId());
        param.add("customer_uid", String.valueOf(order.getUser().getId()));
        param.add("name", order.getPlan().getName());
        param.add("amount", String.valueOf(order.totalAmount()));

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(AGAIN_URI)
                        .build()
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.Again.class);
        return (PortOneResponse.AgainPay) response;
    }

    public PortOneResponse.Cancel cancel(String impUid, String amount) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("imp_uid", impUid);
        param.add("amount", amount);

        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(CANCEL_URI)
                        .build()
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.Cancel.class);
        return (PortOneResponse.Cancel) response;
    }

    public PortOneResponse.IssueBillingKey issueBillingKey(Long userId, Payment payment) {
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("card_number", payment.getCardNumber()); //테스트 시 카드 정보 상관 없음
        param.add("expiry", payment.getExpiry());
        param.add("birth", payment.getBirthDate());
        param.add("pwd_2digit", payment.getPassword());
        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(DELETE_BILLING_KEY)
                        .build(userId)
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(param))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.IssueBillingKey.class);
        return (PortOneResponse.IssueBillingKey) response;
    }

    public PortOneResponse.DeleteBillingKey deleteBillingKey(Long userId) {
        WebClient wc = createWebClient();
        String result = wc.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(HOST)
                        .path(DELETE_BILLING_KEY)
                        .build(userId)
                )
                .header("Authorization", "Bearer " + issueToken().getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        PortOneResponse response = getData(result, PortOneForm.DeleteBillingKey.class);
        return (PortOneResponse.DeleteBillingKey) response;
    }

    private <T extends PortOneForm> PortOneResponse getData(String result, Class<T> form) {
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
}
