package vn.fs.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.entities.Category;
import vn.fs.entities.User;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class CategoryController {

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

	// Đặt thuộc tính categories vào model để hiển thị danh sách danh mục
	@ModelAttribute("categories")
	public List<Category> showCategory(Model model) {
		List<Category> categories = categoryRepository.findAll();
		return categories;
	}

	// Hiển thị trang danh sách các danh mục
	@GetMapping(value = "/categories")
	public String categories(Model model) {
		Category category = new Category();
		model.addAttribute("category", category);
		return "admin/categories";
	}

	// Xử lý thêm mới danh mục
	@PostMapping(value = "/addCategory")
	public String addCategory(@Validated @ModelAttribute("category") Category category, ModelMap model,
							  BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "failure");
			return "admin/categories"; // Nếu có lỗi, trở về trang thêm danh mục
		}

		categoryRepository.save(category); // Lưu danh mục mới vào CSDL
		model.addAttribute("message", "successful!"); // Thông báo thành công

		return "redirect:/admin/categories"; // Chuyển hướng về trang danh sách danh mục
	}

	// Hiển thị trang chỉnh sửa danh mục
	@GetMapping(value = "/editCategory/{id}")
	public String editCategory(@PathVariable("id") Long id, ModelMap model) {
		Category category = categoryRepository.findById(id).orElse(null);
		model.addAttribute("category", category);
		return "admin/editCategory";
	}

	// Xử lý xóa danh mục
	@GetMapping("/delete/{id}")
	public String delCategory(@PathVariable("id") Long id, Model model) {
		categoryRepository.deleteById(id); // Xóa danh mục từ CSDL
		model.addAttribute("message", "Delete successful!"); // Thông báo xóa thành công
		return "redirect:/admin/categories"; // Chuyển hướng về trang danh sách danh mục
	}
}
