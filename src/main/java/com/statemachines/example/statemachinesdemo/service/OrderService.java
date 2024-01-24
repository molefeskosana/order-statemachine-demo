package com.statemachines.example.statemachinesdemo.service;

import com.statemachines.example.statemachinesdemo.entities.OrderEntity;
import com.statemachines.example.statemachinesdemo.enums.OrderEvent;
import com.statemachines.example.statemachinesdemo.enums.OrderState;
import com.statemachines.example.statemachinesdemo.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    public static  final String ORDER_ID_HEADER = "order_id";
    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderState, OrderEvent> factory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;

    @Autowired
    public OrderService(OrderRepository orderRepository, StateMachineFactory<OrderState, OrderEvent> factory, OrderStateChangeInterceptor orderStateChangeInterceptor) {
        this.orderRepository = orderRepository;
        this.factory = factory;
        this.orderStateChangeInterceptor = orderStateChangeInterceptor;
    }

    @Transactional
    public OrderEntity newOrder(Date when){
        return  this.orderRepository.save(new OrderEntity(when, OrderState.SUBMITTED));
    }

    @Transactional
    public StateMachine<OrderState, OrderEvent> payOrder(Long orderId, OrderEvent orderEvent){
        StateMachine<OrderState, OrderEvent> sm = this.build(orderId);
        sendEvent(orderId,sm,OrderEvent.PAY);
        return sm;
    }

    @Transactional
    public StateMachine<OrderState, OrderEvent> shipOrder(Long orderId, OrderEvent orderEvent){
        StateMachine<OrderState, OrderEvent> sm = this.build(orderId);
        sendEvent(orderId,sm,OrderEvent.SHIP);
        return sm;
    }

    @Transactional
    public StateMachine<OrderState, OrderEvent> cancelOrder(Long orderId, OrderEvent orderEvent){
        StateMachine<OrderState, OrderEvent> sm = this.build(orderId);
        sendEvent(orderId,sm,OrderEvent.CANCEL);
        return sm;
    }

    private void sendEvent(Long orderId, StateMachine<OrderState, OrderEvent> sm, OrderEvent event){
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderState, OrderEvent> build(Long orderId) {

        OrderEntity orderEntity = this.orderRepository.getOne(orderId);

        StateMachine<OrderState, OrderEvent> sm = this.factory.getStateMachine(Long.toString(orderEntity.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<OrderState, OrderEvent>(
                            orderEntity.getOrderState(), null, null, null));
                });
        sm.start();

        return  sm;
    }

}
