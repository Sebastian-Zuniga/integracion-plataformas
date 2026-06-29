package com.pcfactory.ecommerce.repository;

import com.pcfactory.ecommerce.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Optional<Transaccion> findByBuyOrder(String buyOrder);
    Optional<Transaccion> findByTokenWebpay(String tokenWebpay);
}