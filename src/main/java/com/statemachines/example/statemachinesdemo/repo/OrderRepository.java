package com.statemachines.example.statemachinesdemo.repo;

import com.statemachines.example.statemachinesdemo.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
}
