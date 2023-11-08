package rhetorike.glot.domain._4order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.util.portone.PortOneClient;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.ServiceTest;

import java.time.Period;

import static org.mockito.BDDMockito.given;

@ServiceTest
class PayServiceTest {

    @InjectMocks
    PayService payService;

    @Mock
    PortOneClient portOneClient;


    @Test
    @DisplayName("[상품 주문]")
    void pay(){
        //given
        User user = Personal.builder().id(1L).build();
        Plan plan = new BasicPlan(null, "test", 100, Period.ofMonths(1));
        Order order = Order.newOrder(user, plan, 1);
        Payment payment = new Payment("", "", "", "");
        given(portOneClient.payAndSaveBillingKey(order, payment)).willReturn(new PortOneResponse.OneTimePay("", "paid", "", ""));

        //when
         payService.pay(order, payment);

        //then
    }
}