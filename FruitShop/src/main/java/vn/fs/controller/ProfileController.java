package vn.fs.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.Order;
import vn.fs.entities.OrderDetail;
import vn.fs.entities.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.UserRepository;

@Controller
public class ProfileController extends CommomController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	CommomDataService commomDataService;

	// Trang cá nhân người dùng
	@GetMapping(value = "/profile")
	public String profile(Model model, Principal principal, User user, Pageable pageable,
						  @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		// Nếu đã đăng nhập
		if (principal != null) {
			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(6);

		// Lấy danh sách đơn hàng phân trang của người dùng
		Page<Order> orderPage = findPaginated(PageRequest.of(currentPage - 1, pageSize), user);

		int totalPages = orderPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		// Thêm dữ liệu chung cần thiết vào model
		commomDataService.commonData(model, user);
		model.addAttribute("orderByUser", orderPage);

		return "web/profile";
	}

	// Phân trang danh sách đơn hàng của người dùng
	public Page<Order> findPaginated(Pageable pageable, User user) {
		// Lấy danh sách đơn hàng của người dùng
		List<Order> orderPage = orderRepository.findOrderByUserId(user.getUserId());

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Order> list;

		// Phân trang dữ liệu
		if (orderPage.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, orderPage.size());
			list = orderPage.subList(startItem, toIndex);
		}

		// Tạo trang dữ liệu mới
		Page<Order> orderPages = new PageImpl<Order>(list, PageRequest.of(currentPage, pageSize), orderPage.size());

		return orderPages;
	}

	// Xem chi tiết đơn hàng
	@GetMapping("/order/detail/{order_id}")
	public ModelAndView detail(Model model, Principal principal, User user, @PathVariable("order_id") Long id) {
		// Nếu đã đăng nhập
		if (principal != null) {
			model.addAttribute("user", new User());
			user = userRepository.findByEmail(principal.getName());
			model.addAttribute("user", user);
		}

		// Lấy danh sách chi tiết đơn hàng
		List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);

		model.addAttribute("orderDetail", listO);
		commomDataService.commonData(model, user);

		return new ModelAndView("web/historyOrderDetail");
	}

	// Hủy đơn hàng
	@RequestMapping("/order/cancel/{order_id}")
	public ModelAndView cancel(ModelMap model, @PathVariable("order_id") Long id) {
		// Tìm đơn hàng cần hủy
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("redirect:/profile", model);
		}

		Order oReal = o.get();
		// Đặt trạng thái của đơn hàng thành đã hủy
		oReal.setStatus((short) 3);
		orderRepository.save(oReal);

		return new ModelAndView("redirect:/profile", model);
	}
}
