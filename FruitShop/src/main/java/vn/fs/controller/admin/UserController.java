package vn.fs.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import vn.fs.entities.User;
import vn.fs.repository.UserRepository;

@Controller
public class UserController {

	@Autowired
	UserRepository userRepository;

	// Hiển thị danh sách người dùng
	@GetMapping(value = "/admin/users")
	@PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ admin mới có quyền truy cập
	public String customer(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);

		return "/admin/users"; // Trả về view để hiển thị danh sách người dùng
	}

	// Khóa tài khoản người dùng
	@PostMapping(value = "/admin/users/{userId}/lock")
	@PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ admin mới có quyền khóa tài khoản
	public String lockUser(@PathVariable("userId") Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setStatus(false); // Khóa tài khoản
			userRepository.save(user);
		}
		return "redirect:/admin/users"; // Sau khi khóa, chuyển hướng về danh sách người dùng
	}

	// Mở khóa tài khoản người dùng
	@PostMapping(value = "/admin/users/{userId}/unlock")
	@PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ admin mới có quyền mở khóa tài khoản
	public String unlockUser(@PathVariable("userId") Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setStatus(true); // Mở khóa tài khoản
			userRepository.save(user);
		}
		return "redirect:/admin/users"; // Sau khi mở khóa, chuyển hướng về danh sách người dùng
	}
}
