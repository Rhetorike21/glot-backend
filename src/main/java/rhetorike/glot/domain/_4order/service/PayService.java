package rhetorike.glot.domain._4order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.OrderStatus;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.PaymentFailedException;
import rhetorike.glot.global.util.portone.PortOneClient;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static rhetorike.glot.domain._4order.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PortOneClient portOneClient;

    public PortOneResponse.OneTimePay pay(Order order, Payment payment) {
        return portOneClient.payAndSaveBillingKey(order, payment);
    }

    public PortOneResponse.AgainPay payAgain(Order order) {
        return portOneClient.payAgain(order);
    }

    public List<PortOneResponse.PayHistory> getHistory(List<Order> orders) {
        List<String> ids = orders.stream().map(Order::getId).toList();
        return portOneClient.getAllPaymentHistory(ids);
    }


    public void changePayMethod(User user, Payment payment) {
        portOneClient.issueBillingKey(user.getId(), payment);
    }

    public void refund(Order order, long amount) {
        portOneClient.cancel(order.getId(), String.valueOf(amount));
    }
}
