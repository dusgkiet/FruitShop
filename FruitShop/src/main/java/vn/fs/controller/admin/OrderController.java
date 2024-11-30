package vn.fs.controller.admin;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.dto.OrderExcelExporter;
import vn.fs.entities.Order;
import vn.fs.entities.OrderDetail;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.OrderDetailService;
import vn.fs.service.SendMailService;

@Controller
@RequestMapping("/admin")
public class OrderController {

	@Autowired
	OrderDetailService orderDetailService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SendMailService sendMailService;

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

	// Xem danh sách đơn hàng
	@GetMapping(value = "/orders")
	public String orders(Model model, Principal principal) {
		List<Order> orderDetails = orderRepository.findAll();
		model.addAttribute("orderDetails", orderDetails);
		return "admin/orders";
	}

	// Xem chi tiết đơn hàng
	@GetMapping("/order/detail/{order_id}")
	public ModelAndView detail(ModelMap model, @PathVariable("order_id") Long id) {
		List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);
		model.addAttribute("amount", orderRepository.findById(id).get().getAmount());
		model.addAttribute("orderDetail", listO);
		model.addAttribute("orderId", id);
		model.addAttribute("menuO", "menu"); // Đánh dấu active cho front-end
		return new ModelAndView("admin/editOrder", model);
	}

	// Hủy đơn hàng
	@RequestMapping("/order/cancel/{order_id}")
	public ModelAndView cancel(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 3); // Đặt trạng thái của đơn hàng thành "Đã hủy"
		orderRepository.save(oReal);
		return new ModelAndView("forward:/admin/orders", model);
	}

	// Xác nhận đơn hàng
	@RequestMapping("/order/confirm/{order_id}")
	public ModelAndView confirm(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 1); // Đặt trạng thái của đơn hàng thành "Đã xác nhận"
		orderRepository.save(oReal);
		return new ModelAndView("forward:/admin/orders", model);
	}

	// Giao hàng đơn hàng
	@RequestMapping("/order/delivered/{order_id}")
	public ModelAndView delivered(ModelMap model, @PathVariable("order_id") Long id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 2); // Đặt trạng thái của đơn hàng thành "Đã giao hàng"
		orderRepository.save(oReal);

		// Cập nhật số lượng sản phẩm sau khi giao hàng
		Product p = null;
		List<OrderDetail> listDe = orderDetailRepository.findByOrderId(id);
		for (OrderDetail od : listDe) {
			p = od.getProduct();
			p.setQuantity(p.getQuantity() - od.getQuantity()); // Giảm số lượng sản phẩm trong kho
			productRepository.save(p);
		}

		return new ModelAndView("forward:/admin/orders", model);
	}

	// Xuất danh sách đơn hàng ra file Excel
	@GetMapping(value = "/export")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=donHang.xlsx"; // Tên file Excel xuất ra

		response.setHeader(headerKey, headerValue);

		List<Order> lisOrders = orderDetailService.listAll();

		// Sử dụng OrderExcelExporter để xuất danh sách đơn hàng ra file Excel
		OrderExcelExporter excelExporter = new OrderExcelExporter(lisOrders);
		excelExporter.export(response);
	}

}
