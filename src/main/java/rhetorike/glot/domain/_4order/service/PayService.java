package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.OrderStatus;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.PaymentFailedException;
import rhetorike.glot.global.util.portone.PortOneClient;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static rhetorike.glot.domain._4order.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PortOneClient portOneClient;

    public void pay(Order order, Payment payment) {
        PortOneResponse.OneTimePay response = portOneClient.payAndSaveBillingKey(order, payment);
        OrderStatus payStatus = OrderStatus.findByName(response.getStatus());
        if (payStatus == FAILED){
            throw new PaymentFailedException(response.getFail_reason());
        }
        if (payStatus == CANCELLED){
            throw new PaymentFailedException(response.getCancel_reason());
        }
        order.complete();
    }

    public List<OrderDto.GetResponse> getHistory(List<Order> orders) {
        List<String> ids = orders.stream().map(Order::getId).toList();
        Map<String, String> cardNumbers = portOneClient.getAllPaymentHistory(ids).stream()
                .collect(Collectors.toMap(PortOneResponse.PayHistory::getMerchantUid, PortOneResponse.PayHistory::getCardNumber));
        return orders.stream()
                .map(order -> new OrderDto.GetResponse(order, cardNumbers.getOrDefault(order.getId(), "")))
                .toList();
    }
}
