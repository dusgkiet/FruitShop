package vn.fs.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.Favorite;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;

@Controller
public class HomeController extends CommomController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CommomDataService commomDataService;

	@Autowired
	FavoriteRepository favoriteRepository;

	// Mapping cho đường dẫn "/"
	@GetMapping(value = "/")
	public String home(Model model, User user) {
		// Cung cấp dữ liệu chung cho view
		commomDataService.commonData(model, user);

		// Lấy danh sách 20 sản phẩm bán chạy nhất và đánh dấu sản phẩm yêu thích của người dùng
		bestSaleProduct20(model, user);

		// Trả về tên của view, ở đây là "web/home"
		return "web/home";
	}

	// Attribute model để hiển thị danh sách 10 sản phẩm mới nhất
	@ModelAttribute("listProduct10")
	public List<Product> listproduct10(Model model) {
		List<Product> productList = productRepository.listProductNew10();
		model.addAttribute("productList", productList);
		return productList;
	}

	// Phương thức để lấy danh sách 20 sản phẩm bán chạy nhất
	public void bestSaleProduct20(Model model, User customer) {
		List<Object[]> productList = productRepository.bestSaleProduct20();
		if (productList != null) {
			ArrayList<Integer> listIdProductArrayList = new ArrayList<>();
			for (int i = 0; i < productList.size(); i++) {
				String id = String.valueOf(productList.get(i)[0]);
				listIdProductArrayList.add(Integer.valueOf(id));
			}
			List<Product> listProducts = productRepository.findByInventoryIds(listIdProductArrayList);

			List<Product> listProductNew = new ArrayList<>();

			for (Product product : listProducts) {
				Product productEntity = new Product();
				BeanUtils.copyProperties(product, productEntity);

				// Kiểm tra xem sản phẩm đã được người dùng yêu thích hay chưa

				// ----------đã fix --------
				if (customer != null) {
					// Lấy thông tin yêu thích của sản phẩm bởi khách hàng
					Favorite save = favoriteRepository.selectSaves(productEntity.getProductId(), customer.getUserId());

					// Sử dụng toán tử điều kiện để thiết lập giá trị favorite
					productEntity.favorite = (save != null);
				} else {
					// Xử lý khi customer là null
					productEntity.favorite = false;
					// Bạn có thể thêm log hoặc xử lý khác ở đây nếu cần
				}

				// Thêm sản phẩm vào danh sách sản phẩm mới
				listProductNew.add(productEntity);

			}

			model.addAttribute("bestSaleProduct20", listProductNew);
		}
	}
}
