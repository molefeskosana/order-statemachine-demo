package com.statemachines.example.statemachinesdemo.configs;

import com.statemachines.example.statemachinesdemo.enums.OrderEvent;
import com.statemachines.example.statemachinesdemo.enums.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class OrderStatemachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception{
        StateMachineListenerAdapter<OrderState, OrderEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                log.info(String.format("StateChanged(from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration()
                .autoStartup(false)
                .listener(adapter);
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws  Exception{
            states
                .withStates()
                .initial(OrderState.SUBMITTED)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.SHIPPED)
                .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception{
        transitions
                .withExternal()
                .source(OrderState.SUBMITTED).target(OrderState.PAID).event(OrderEvent.PAY)
                .and()
                .withExternal()
                .source(OrderState.PAID).target(OrderState.SHIPPED).event(OrderEvent.SHIP)
                .and()
                .withExternal()
                .source(OrderState.SUBMITTED).target(OrderState.CANCELLED).event(OrderEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderState.PAID).target(OrderState.CANCELLED).event(OrderEvent.CANCEL);

    }
}
