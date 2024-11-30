package vn.fs.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import vn.fs.entities.CartItem;
import vn.fs.entities.Product;
import vn.fs.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private Map<Long, CartItem> map = new HashMap<Long, CartItem>(); // Dùng để lưu trữ các sản phẩm trong giỏ hàng

	// Phương thức thêm sản phẩm vào giỏ hàng
	@Override
	public void add(CartItem item) {
		CartItem existedItem = map.get(item.getId());

		if (existedItem != null) {
			// Nếu sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng và tổng giá tiền
			existedItem.setQuantity(item.getQuantity() + existedItem.getQuantity());
			existedItem.setTotalPrice(item.getTotalPrice() + existedItem.getUnitPrice() * item.getQuantity());
		} else {
			// Nếu sản phẩm chưa tồn tại trong giỏ hàng, thêm vào map
			map.put(item.getId(), item);
		}
	}

	// Phương thức xóa sản phẩm khỏi giỏ hàng
	@Override
	public void remove(CartItem item) {
		map.remove(item.getId());
	}

	// Phương thức lấy danh sách các sản phẩm trong giỏ hàng
	@Override
	public Collection<CartItem> getCartItems() {
		return map.values();
	}

	// Phương thức xóa toàn bộ giỏ hàng
	@Override
	public void clear() {
		map.clear();
	}

	// Phương thức tính tổng số tiền của giỏ hàng
	@Override
	public double getAmount() {
		return map.values().stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
	}

	// Phương thức lấy số lượng sản phẩm trong giỏ hàng
	@Override
	public int getCount() {
		return map.size();
	}

	// Phương thức xóa sản phẩm khỏi giỏ hàng theo đối tượng Product (chưa được triển khai)
	@Override
	public void remove(Product product) {
		// Chưa được triển khai
	}

	// Phương thức cập nhật số lượng sản phẩm trong giỏ hàng
	@Override
	public void updateQuantity(Long productId, int newQuantity) {
		CartItem item = map.get(productId);
		if (item != null) {
			// Nếu sản phẩm tồn tại trong giỏ hàng, cập nhật số lượng và tổng giá tiền
			item.setQuantity(newQuantity);
			item.setTotalPrice(item.getUnitPrice() * newQuantity);
		}
	}
}
