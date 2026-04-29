package io.github.vikij.ordermanagement.order.repository;

import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items"})
    Page<Order> findByCreatedBy(AppUser user, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    java.util.List<Order> findByCreatedBy(AppUser user);

    @EntityGraph(attributePaths = {"items"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findByOrderNumber(String orderNumber);
}
