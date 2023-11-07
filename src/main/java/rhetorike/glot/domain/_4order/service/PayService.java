package rhetorike.glot.domain._4order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.portone.PortOneClient;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PortOneClient portOneClient;
    public void pay(Order order, Payment payment) {
        portOneClient.payAndSaveBillingKey(order, payment);
    }
}
