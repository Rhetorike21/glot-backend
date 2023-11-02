package rhetorike.glot.global.util.portone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

public class PortOneResponse {


    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class Token extends PortOneResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class OneTimePay extends PortOneResponse {
        @JsonProperty("imp_uid")
        private String impUid;
        private String pay_method;
        private String bank_code;
        private String bank_name;
        private String buyer_name;
        private String client_uid;
        private String customer_uid;
        private String status;
        private String fail_reason;
        private String cancel_reason;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class PayHistory extends PortOneResponse {
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
    public static class AgainPay extends PortOneResponse {
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
    public static class Cancel extends PortOneResponse {
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
    public static class IssueBillingKey extends PortOneResponse {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    @Getter
    public static class DeleteBillingKey extends PortOneResponse{
    }

}