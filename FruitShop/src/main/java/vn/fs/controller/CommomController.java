package vn.fs.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.fs.entities.Category;
import vn.fs.entities.User;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;

@Controller
public class CommomController {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;

	// Lấy thông tin người dùng đăng nhập
	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal) {
		User user = null;
		if (principal != null) {
			String userEmail = principal.getName(); // Lấy email của người dùng từ Principal
			user = userRepository.findByEmail(userEmail);
		}
		return user;
	}

	// Hiển thị danh sách các danh mục sản phẩm
	@ModelAttribute("categoryList")
	public List<Category> showCategory(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return categoryList;
	}
}
