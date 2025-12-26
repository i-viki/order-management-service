package io.github.vikij.ordermanagement.order.repository;

import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCreatedBy(AppUser user);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("""
    select distinct o
    from Order o
    left join fetch o.items
    where o.createdBy = :user
""")
List<Order> findWithItemsByCreatedBy(@Param("user") AppUser user);

    @Query("""
    select distinct o
    from Order o
    left join fetch o.items
""")
List<Order> findAllWithItems();
    @Query("""
    select o
    from Order o
    left join fetch o.items
    where o.orderNumber = :orderNumber
""")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

}
