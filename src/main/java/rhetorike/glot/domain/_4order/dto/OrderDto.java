package rhetorike.glot.domain._4order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.portone.PortOneForm;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MakeRequest {
        private Long planId;
        private int quantity;
        private Payment payment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetResponse {
        private LocalDate payDate;
        private String duration;
        private String cardNumber;
        private long amount;
        private long surtax;
        private String status;

        public GetResponse(Order order) {
            this.payDate = null;
            this.duration = null;
            this.cardNumber = null;
            this.amount = 0;
            this.surtax = 0;
        }

        public GetResponse(Order order, String cardNumber) {
            LocalDate startDate = order.getCreatedTime().toLocalDate();
            LocalDate endDate = startDate.plus(order.getPlan().getExpiryPeriod()).minusDays(1);
            this.payDate = startDate;
            this.duration = startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + endDate.format(DateTimeFormatter.ofPattern(" ~ yyyy년 MM월 dd일"));
            this.cardNumber = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
            this.amount = order.getTotalPrice();
            this.surtax = order.getVat();
            this.status = order.getStatus().getDescription();
        }
    }
}
