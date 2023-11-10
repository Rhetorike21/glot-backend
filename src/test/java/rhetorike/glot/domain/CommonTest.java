package rhetorike.glot.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.Plan;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.PayService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.PaymentFailedException;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
public class CommonTest extends IntegrationTest {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @MockBean
    PayService payService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("결제에 실패해도 주문 데이터가 생성된다. ")
    void test() {
        //given
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        User user = Personal.builder().build();
        Plan plan = new BasicPlan(null, null, 0, PlanPeriod.MONTH);
        planRepository.save(plan);
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "failed", "", ""));
        Long userId = userRepository.save(user).getId();

        //when
        Assertions.assertThatThrownBy(() -> orderService.makeBasicOrder(new OrderDto.BasicOrderRequest(PlanPeriod.MONTH.getName(), payment), userId)).isInstanceOf(PaymentFailedException.class);

        //then
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);
        log.info("{}", orders);
        assertThat(orders).isNotEmpty();
    }
}
