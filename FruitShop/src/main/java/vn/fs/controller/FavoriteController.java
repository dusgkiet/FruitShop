package vn.fs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.Favorite;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;

@Controller
public class FavoriteController extends CommomController {

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CommomDataService commomDataService;

	// Mapping cho đường dẫn /favorite
	@GetMapping(value = "/favorite")
	public String favorite(Model model, User user) {
		// Lấy danh sách các sản phẩm yêu thích của người dùng
		List<Favorite> favorites = favoriteRepository.selectAllSaves(user.getUserId());

		// Cung cấp dữ liệu chung cho view
		commomDataService.commonData(model, user);

		// Thêm danh sách các sản phẩm yêu thích vào model để hiển thị trên view
		model.addAttribute("favorites", favorites);

		// Trả về tên của view, ở đây là "web/favorite"
		return "web/favorite";
	}

	// Mapping cho đường dẫn /doFavorite
	@GetMapping(value = "/doFavorite")
	public String doFavorite(Model model, Favorite favorite, User user, @RequestParam("id") Long id) {
		// Lấy thông tin sản phẩm theo id
		Product product = productRepository.findById(id).orElse(null);

		// Thiết lập các thuộc tính cho đối tượng favorite
		favorite.setProduct(product);
		favorite.setUser(user);
		product.setFavorite(true); // Đánh dấu sản phẩm là yêu thích

		// Lưu thông tin sản phẩm yêu thích vào repository
		favoriteRepository.save(favorite);

		// Cung cấp dữ liệu chung cho view
		commomDataService.commonData(model, user);

		// Redirect về trang danh sách sản phẩm sau khi thực hiện thao tác yêu thích
		return "redirect:/products";
	}

	// Mapping cho đường dẫn /doUnFavorite
	@GetMapping(value = "/doUnFavorite")
	public String doUnFavorite(Model model, Product product, User user, @RequestParam("id") Long id) {
		// Tìm thông tin sản phẩm yêu thích để xóa
		Favorite favorite = favoriteRepository.selectSaves(id, user.getUserId());
		product = productRepository.findById(id).orElse(null);
		product.setFavorite(false); // Bỏ đánh dấu sản phẩm là yêu thích

		// Xóa sản phẩm yêu thích khỏi repository
		favoriteRepository.delete(favorite);

		// Cung cấp dữ liệu chung cho view
		commomDataService.commonData(model, user);

		// Redirect về trang danh sách sản phẩm sau khi thực hiện hủy yêu thích
		return "redirect:/products";
	}
}
