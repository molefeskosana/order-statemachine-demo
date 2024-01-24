package com.statemachines.example.statemachinesdemo.entities;

import com.statemachines.example.statemachinesdemo.enums.OrderState;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Date datetime;
    private String state;

    public OrderEntity(Date datetime, OrderState orderState){
        this.datetime = datetime;
        setOrderState(orderState);
    }

    public OrderState getOrderState(){
        return OrderState.valueOf(this.state);
    }

    public void setOrderState(OrderState orderState){
        this.state = orderState.name();
    }
}
