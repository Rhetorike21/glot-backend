package rhetorike.glot.global.util.portone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class PortOneResponse {


    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class Token extends PortOneResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class OneTimePay extends PortOneResponse {
        @JsonProperty("imp_uid")
        private String impUid;
        private String status;
        private String fail_reason;
        private String cancel_reason;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class PayHistory extends PortOneResponse {
        @JsonProperty("imp_uid")
        private String impUid;
        @JsonProperty("merchant_uid")
        private String merchantUid;
        @JsonProperty("card_number")
        private String cardNumber;
        private String status;
        private long amount;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class AgainPay extends PortOneResponse {
        @JsonProperty("imp_uid")
        private String impUid;
        private String status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class Cancel extends PortOneResponse {
        @JsonProperty("imp_uid")
        private String impUid;
        private String status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class IssueBillingKey extends PortOneResponse {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class DeleteBillingKey extends PortOneResponse{
    }
}