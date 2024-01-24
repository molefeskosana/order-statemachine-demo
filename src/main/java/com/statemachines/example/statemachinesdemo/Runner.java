package com.statemachines.example.statemachinesdemo;

import com.statemachines.example.statemachinesdemo.entities.OrderEntity;
import com.statemachines.example.statemachinesdemo.enums.OrderEvent;
import com.statemachines.example.statemachinesdemo.enums.OrderState;
import com.statemachines.example.statemachinesdemo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class Runner implements ApplicationRunner {

    private final OrderService orderService;

    public Runner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception{
        //Make a new order
        OrderEntity orderEntity = this.orderService.newOrder(new Date());

        //Pay for your order
        this.orderService.payOrder(orderEntity.getId(), OrderEvent.PAY);

        //Shipment of an order
        this.orderService.shipOrder(orderEntity.getId(), OrderEvent.SHIP);

        //Order cancellation
        //this.orderService.cancelOrder(orderEntity.getId(), OrderEvent.CANCEL);
    }
}
