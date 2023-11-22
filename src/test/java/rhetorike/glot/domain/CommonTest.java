package rhetorike.glot.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import rhetorike.glot.domain._2user.controller.UserController;
import rhetorike.glot.domain._2user.dto.UserDto;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.BasicPlan;
import rhetorike.glot.domain._4order.entity.Order;
import rhetorike.glot.domain._4order.entity.PlanPeriod;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.service.OrderService;
import rhetorike.glot.domain._4order.service.PayService;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.error.exception.PaymentFailedException;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
        BasicPlan plan = planRepository.save(new BasicPlan(null, "베이직 요금제 월간 결제", 100L, 100L, PlanPeriod.MONTH));

        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay("", "failed", "", ""));
        Long userId = userRepository.save(user).getId();

        //when
        Assertions.assertThatThrownBy(() -> orderService.makeOrder(new OrderDto.MakeRequest(plan.getId(), 1, payment), userId)).isInstanceOf(PaymentFailedException.class);

        //then
        List<Order> orders = orderRepository.findByUserOrderByCreatedTimeDesc(user);
        log.info("{}", orders);
        assertThat(orders).isNotEmpty();
    }


}
