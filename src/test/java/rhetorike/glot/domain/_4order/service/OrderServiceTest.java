package rhetorike.glot.domain._4order.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rhetorike.glot.domain._2user.entity.Organization;
import rhetorike.glot.domain._2user.entity.Personal;
import rhetorike.glot.domain._2user.entity.User;
import rhetorike.glot.domain._2user.reposiotry.UserRepository;
import rhetorike.glot.domain._4order.dto.OrderDto;
import rhetorike.glot.domain._4order.entity.*;
import rhetorike.glot.domain._4order.repository.PlanRepository;
import rhetorike.glot.domain._4order.repository.OrderRepository;
import rhetorike.glot.domain._4order.repository.SubscriptionRepository;
import rhetorike.glot.domain._4order.vo.Payment;
import rhetorike.glot.global.error.exception.AccessDeniedException;
import rhetorike.glot.global.util.portone.PortOneResponse;
import rhetorike.glot.setup.ServiceTest;

import java.sql.Ref;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ServiceTest
class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PlanRepository planRepository;
    @Mock
    PayService payService;
    @Mock
    SubscriptionService subscriptionService;
    @Mock
    RefundService refundService;
    @Mock
    SubscriptionRepository subscriptionRepository;

    @Test
    @DisplayName("[베이직 요금제 주문]")
    void makeBasicOrder() {
        //given
        Long userId = 1L;
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        User user = Personal.builder().build();
        BasicPlan plan = new BasicPlan();
        given(planRepository.findBasicByPlanPeriod(PlanPeriod.MONTH)).willReturn(Optional.of(plan));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.save(any())).willReturn(new Order());
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay(null, "paid", null, null));

        //when
        orderService.makeBasicOrder(new OrderDto.BasicOrderRequest(PlanPeriod.MONTH.getName(), payment), userId);

        //then
        verify(planRepository).findBasicByPlanPeriod(PlanPeriod.MONTH);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 주문]")
    void makeEnterpriseOrder() {
        //given
        Long userId = 1L;
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        User user = Organization.builder().build();
        EnterprisePlan plan = new EnterprisePlan();
        given(planRepository.findEnterpriseByPlanPeriod(PlanPeriod.MONTH)).willReturn(Optional.of(plan));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.save(any())).willReturn(new Order());
        given(payService.pay(any(), any())).willReturn(new PortOneResponse.OneTimePay(null, "paid", null, null));

        //when
        orderService.makeEnterpriseOrder(new OrderDto.EnterpriseOrderRequest(PlanPeriod.MONTH.getName(), 3, payment), userId);

        //then
        verify(planRepository).findEnterpriseByPlanPeriod(PlanPeriod.MONTH);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("[엔터프라이즈 요금제 주문] - 조직 계정만 구매 가능")
    void makeEnterpriseOrderOnlyForOrganization() {
        //given
        Long userId = 1L;
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        User user = Personal.builder().build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        Assertions.assertThatThrownBy(() -> orderService.makeEnterpriseOrder(new OrderDto.EnterpriseOrderRequest(PlanPeriod.MONTH.getName(), 3, payment), userId))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @DisplayName("[주문 내역 조회]")
    void getOrders() {
        //given
        Long userId = 1L;
        User user = Personal.builder().build();
        Plan plan = BasicPlan.builder().expiryPeriod(PlanPeriod.MONTH).build();
        Subscription subscription = Subscription.builder().startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Order order = Order.builder().status(OrderStatus.PAID).plan(plan).subscription(subscription).build();
        order.setCreatedTime(LocalDateTime.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.findByUserOrderByCreatedTimeDesc(user)).willReturn(List.of(order));
        given(payService.getHistory(List.of(order))).willReturn(List.of(new PortOneResponse.PayHistory(null, null, "1234123412341234", "paid", 100)));

        //when
        orderService.getOrders(userId);

        //then
        verify(userRepository).findById(userId);
        verify(orderRepository).findByUserOrderByCreatedTimeDesc(user);
    }

    @Test
    @DisplayName("[지불 방식 변경]")
    void changePayMethod() {
        //given
        Long userId = 1L;
        User user = Personal.builder().build();
        Payment payment = new Payment("1234-1234-1234-1234", "2028-07", "990311", "11");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        orderService.changePayMethod(payment, userId);

        //then
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("[환불]")
    void refund() {
        //given
        Long userId = 1L;
        User user = Personal.builder().build();
        Subscription subscription = Subscription.builder().order(new Order()).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(subscriptionRepository.findByOrderer(user)).willReturn(Optional.of(subscription));
        given(refundService.calcRefundAmount(subscription)).willReturn(100L);

        //when
        orderService.refund(userId);

        //then
        verify(userRepository).findById(userId);
        verify(subscriptionRepository).findByOrderer(user);
        verify(refundService).calcRefundAmount(subscription);
    }

    @Test
    @DisplayName("[환불 정보 조회]")
    void getRefundInfo() {
        //given
        Long userId = 1L;
        User user = Personal.builder().build();
        Plan plan = BasicPlan.builder().price(30000L).discountedPrice(18000L).expiryPeriod(PlanPeriod.MONTH).build();
        Order order = Order.newOrder(user, plan, 1);
        order.setCreatedTime(LocalDateTime.now());
        Subscription subscription = Subscription.newSubscription(order);
        user.setSubscription(subscription);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(subscriptionRepository.findByOrderer(user)).willReturn(Optional.of(subscription));
        given(refundService.calcRefundAmount(subscription)).willReturn(100L);

        //when
        orderService.getRefundInfo(userId);

        //then
        verify(userRepository).findById(userId);
        verify(refundService).calcRefundAmount(subscription);
    }

}