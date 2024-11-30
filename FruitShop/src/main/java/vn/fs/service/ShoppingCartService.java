package vn.fs.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import vn.fs.entities.CartItem;
import vn.fs.entities.Product;

@Service
public interface ShoppingCartService {

	// Lấy số lượng các mặt hàng trong giỏ hàng
	int getCount();

	// Lấy tổng số tiền của các mặt hàng trong giỏ hàng
	double getAmount();

	// Xóa tất cả các mặt hàng trong giỏ hàng
	void clear();

	// Lấy danh sách các mặt hàng trong giỏ hàng
	Collection<CartItem> getCartItems();

	// Xóa một mặt hàng khỏi giỏ hàng
	void remove(CartItem item);

	// Thêm một mặt hàng vào giỏ hàng
	void add(CartItem item);

	// Cập nhật số lượng của một mặt hàng trong giỏ hàng
	void updateQuantity(Long productId, int newQuantity);

	// Xóa một mặt hàng khỏi giỏ hàng dựa trên đối tượng Product
	void remove(Product product);
}
