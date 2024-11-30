package vn.fs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fs.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	// Tìm các đơn hàng theo user_id
	@Query(value = "select * from orders where user_id = ?1", nativeQuery = true)
	List<Order> findOrderByUserId(Long id);

	// Tính tổng doanh thu của các đơn hàng đã hoàn thành (status = 2)
	@Query("SELECT SUM(o.amount) FROM Order o WHERE o.status = 2")
	Double findTotalRevenue();

	// Đếm số lượng đơn hàng đã thành công (status = 2)
	@Query("SELECT COUNT(o) FROM Order o WHERE o.status = 2")
	Long countSuccessfulOrders();

	// Đếm số lượng đơn hàng đã hủy (status = 3)
	@Query("SELECT COUNT(o) FROM Order o WHERE o.status = 3")
	Long countCancelledOrders();

	// Đếm số lượng đơn hàng mới (status = 0)
	@Query("SELECT COUNT(o) FROM Order o WHERE o.status = 0")
	Long countNewOrders();
}
