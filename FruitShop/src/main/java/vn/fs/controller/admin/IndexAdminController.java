package vn.fs.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.fs.entities.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class IndexAdminController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	// Đặt thuộc tính user vào model để hiển thị thông tin người dùng đăng nhập
	@ModelAttribute(value = "user")
	public User user(Model model, Principal principal) {
		User user = null;
		if (principal != null) {
			user = userRepository.findByEmail(principal.getName());
		}
		return user;
	}

	@GetMapping(value = "/home")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
	public String showDashboard(Model model) {

		// Lấy danh sách thống kê doanh thu theo tháng từ repository
		List<Object[]> monthlyStats = orderDetailRepository.repoWhereMonth();

		// Chuyển đổi danh sách monthlyStats thành chuỗi JSON
		ObjectMapper objectMapper = new ObjectMapper();
		String monthlyStatsJson;
		try {
			monthlyStatsJson = objectMapper.writeValueAsString(monthlyStats);
		} catch (JsonProcessingException e) {
			monthlyStatsJson = "[]"; // Nếu có lỗi, trả về một mảng JSON rỗng
		}

		// Truyền mảng JSON vào model
		model.addAttribute("monthlyStats", monthlyStatsJson);

		// Truyền các thông tin thống kê khác vào model
		model.addAttribute("totalRevenue", orderRepository.findTotalRevenue());
		model.addAttribute("successfulOrders", orderRepository.countSuccessfulOrders());
		model.addAttribute("cancelledOrders", orderRepository.countCancelledOrders());
		model.addAttribute("totalUsers", userRepository.count());
		model.addAttribute("newOrders", orderRepository.countNewOrders());

		return "admin/index"; // Trả về tên của file template trong thư mục templates/admin
	}
}
