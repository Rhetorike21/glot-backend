package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.portone.PortOneClient;
import rhetorike.glot.global.util.portone.PortOneResponse;

import java.util.List;

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
        portOneClient.issueBillingKey(user, payment);
    }

    public void refund(Order order, long amount) {
        portOneClient.cancel(order.getId(), String.valueOf(amount));
    }

    public PortOneResponse.PayMethod getPayMethod(User user) {
        return portOneClient.getBillingKey(user.getId());
    }
}
