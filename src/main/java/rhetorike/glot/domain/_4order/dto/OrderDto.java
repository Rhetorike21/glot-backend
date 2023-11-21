package rhetorike.glot.domain._4order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.OrderStatus;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicOrderRequest {
        private String planPeriod;
        private Payment payment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnterpriseOrderRequest {
        private String planPeriod;
        private int quantity;
        private Payment payment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        private LocalDate paidDate;
        private String duration;
        private String cardNumber;
        private Long amount;
        private Long surtax;
        private String status;

        public static History from(Order order, PortOneResponse.PayHistory history) {
            OrderStatus status = order.getStatus();
            LocalDate startDate = order.getCreatedTime().toLocalDate();
            LocalDate endDate = order.getPlan().endDateFrom(order.getCreatedTime().toLocalDate());
            LocalDate payDate = order.getCreatedTime().toLocalDate();
            String cardNumber = history.getCardNumber();
            return History.builder()
                    .paidDate(payDate)
                    .duration(startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + endDate.format(DateTimeFormatter.ofPattern(" ~ yyyy년 MM월 dd일")))
                    .cardNumber("****-****-****-" + cardNumber.substring(cardNumber.length() - 4))
                    .amount(order.getTotalPrice())
                    .surtax(order.getVat())
                    .status(status.getDescription())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetResponse{
        private String plan;
        private String status;
        private String payMethod;
        @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
        private String payPeriod;
        private LocalDate nextPayDate;
        private LocalDate firstPaidDate;
        private List<History> history;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefundResponse{
        String accountId;
        int numOfMembers;
        int remainDays;
        long refundAmount;
    }
}
