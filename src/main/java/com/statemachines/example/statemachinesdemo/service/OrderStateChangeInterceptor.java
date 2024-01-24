package com.statemachines.example.statemachinesdemo.service;

import com.statemachines.example.statemachinesdemo.entities.OrderEntity;
import com.statemachines.example.statemachinesdemo.enums.OrderEvent;
import com.statemachines.example.statemachinesdemo.enums.OrderState;
import com.statemachines.example.statemachinesdemo.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.statemachines.example.statemachinesdemo.service.OrderService.ORDER_ID_HEADER;

@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

    private final OrderRepository orderRepository;

    @Override
    public void preStateChange(
            State<OrderState, OrderEvent> state,
            Message<OrderEvent> message,
            Transition<OrderState, OrderEvent> transition,
            StateMachine<OrderState,
            OrderEvent> stateMachine,
            StateMachine<OrderState, OrderEvent> rootStateMachine) {

        Optional.ofNullable(message)
                .ifPresent(msg ->{
                    Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L)))
                                        .ifPresent(orderId -> {
                                            OrderEntity order = orderRepository.getOne(orderId);
                                            order.setOrderState(state.getId());
                                            orderRepository.save(order);
                                        });
        });

    }
}
