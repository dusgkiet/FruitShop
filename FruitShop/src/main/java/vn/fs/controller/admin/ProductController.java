package vn.fs.controller.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.fs.entities.Category;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class ProductController {

	@Value("${upload.path}")
	private String pathUploadImage; // Đường dẫn để lưu trữ hình ảnh sản phẩm

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;

	// Đặt thuộc tính user vào model để hiển thị thông tin người dùng đăng nhập
	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal) {
		User user = null;
		if (principal != null) {
			user = userRepository.findByEmail(principal.getName());
		}
		return user;
	}

	// Xem danh sách sản phẩm
	@ModelAttribute("products")
	public List<Product> showProduct(Model model) {
		List<Product> products = productRepository.findAll();
		model.addAttribute("products", products);
		return products;
	}

	// Xem danh sách sản phẩm - hiển thị trang quản lý sản phẩm
	@GetMapping(value = "/products")
	public String products(Model model, Principal principal) {
		Product product = new Product();
		model.addAttribute("product", product);
		return "admin/products";
	}

	// Thêm sản phẩm mới
	@PostMapping(value = "/addProduct")
	public String addProduct(@ModelAttribute("product") Product product, ModelMap model,
							 @RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {

		try {
			// Lưu file ảnh sản phẩm lên server
			File convFile = new File(pathUploadImage + "/" + file.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			// Xử lý nếu có lỗi khi lưu file
			e.printStackTrace();
		}

		// Thiết lập tên file ảnh cho sản phẩm và lưu sản phẩm vào cơ sở dữ liệu
		product.setProductImage(file.getOriginalFilename());
		Product p = productRepository.save(product);

		// Kiểm tra việc lưu sản phẩm thành công hay không và cập nhật thông báo vào model
		if (p != null) {
			model.addAttribute("message", "Update success");
		} else {
			model.addAttribute("message", "Update failure");
		}
		return "redirect:/admin/products";
	}

	// Hiển thị danh sách các danh mục sản phẩm (cho select option trong form thêm sản phẩm)
	@ModelAttribute("categoryList")
	public List<Category> showCategory(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return categoryList;
	}

	// Chỉnh sửa sản phẩm
	@GetMapping(value = "/editProduct/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		Product product = productRepository.findById(id).orElse(null);
		model.addAttribute("product", product);
		return "admin/editProduct";
	}

	// Cập nhật thông tin sản phẩm đã chỉnh sửa
	@PostMapping(value = "/updateProduct")
	public String updateProduct(@ModelAttribute("product") Product product, Model model,
								@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {

		// Kiểm tra nếu có file ảnh được tải lên
		if (!file.isEmpty()) {
			try {
				// Lưu file ảnh mới lên server
				File convFile = new File(pathUploadImage + "/" + file.getOriginalFilename());
				FileOutputStream fos = new FileOutputStream(convFile);
				fos.write(file.getBytes());
				fos.close();

				// Cập nhật tên file ảnh mới cho sản phẩm
				product.setProductImage(file.getOriginalFilename());
			} catch (IOException e) {
				// Xử lý nếu có lỗi khi lưu file
				e.printStackTrace();
			}
		}

		// Lưu thông tin sản phẩm vào cơ sở dữ liệu
		Product updatedProduct = productRepository.save(product);

		// Kiểm tra xem việc cập nhật sản phẩm thành công hay không và chuyển hướng tới trang danh sách sản phẩm
		if (updatedProduct != null) {
			model.addAttribute("message", "Update success");
		} else {
			model.addAttribute("message", "Update failure");
		}
		return "redirect:/admin/products";
	}

	// Xóa sản phẩm
	@GetMapping("/deleteProduct/{id}")
	public String delProduct(@PathVariable("id") Long id, Model model) {
		productRepository.deleteById(id);
		model.addAttribute("message", "Delete successful!");
		return "redirect:/admin/products";
	}

	// Định dạng ngày tháng
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
}
