package vn.fs.controller;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import vn.fs.commom.CommomDataService;
import vn.fs.config.PaypalPaymentIntent;
import vn.fs.config.PaypalPaymentMethod;
import vn.fs.entities.CartItem;
import vn.fs.entities.Order;
import vn.fs.entities.OrderDetail;
import vn.fs.entities.Product;
import vn.fs.entities.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;
import vn.fs.service.PaypalService;
import vn.fs.service.ShoppingCartService;
import vn.fs.util.Utils;

@Controller
public class CartController extends CommomController {

	@Autowired
	HttpSession session;

	@Autowired
	CommomDataService commomDataService;

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	private PaypalService paypalService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	// Đối tượng Order để lưu trữ thông tin đơn hàng cuối cùng
	public Order orderFinal = new Order();

	// URL thành công và hủy bỏ thanh toán PayPal
	public static final String URL_PAYPAL_SUCCESS = "pay/success";
	public static final String URL_PAYPAL_CANCEL = "pay/cancel";
	private Logger log = LoggerFactory.getLogger(getClass());

	// Thêm một sản phẩm vào giỏ hàng
	@GetMapping(value = "/addToCart")
	public String add(@RequestParam("productId") Long productId, HttpServletRequest request, Model model) {

		// Lấy thông tin sản phẩm từ productId
		Product product = productRepository.findById(productId).orElse(null);

		// Lấy giỏ hàng từ session hiện tại
		session = request.getSession();
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();

		// Nếu sản phẩm tồn tại, thêm vào giỏ hàng
		if (product != null) {
			CartItem item = new CartItem();
			BeanUtils.copyProperties(product, item);
			item.setQuantity(1);
			item.setProduct(product);
			item.setId(productId);
			shoppingCartService.add(item);
		}

		// Lưu lại danh sách cartItems vào session và cập nhật số lượng sản phẩm trong giỏ hàng
		session.setAttribute("cartItems", cartItems);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		// Chuyển hướng người dùng đến trang danh sách sản phẩm
		return "redirect:/products";
	}

	// Xóa một sản phẩm khỏi giỏ hàng
	@GetMapping(value = "/remove/{id}")
	public String remove(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
		// Lấy danh sách sản phẩm trong giỏ hàng
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		session = request.getSession();

		// Tìm và xóa CartItem từ id
		Optional<CartItem> optionalCartItem = cartItems.stream()
				.filter(item -> item.getId().equals(id))
				.findFirst();
		if (optionalCartItem.isPresent()) {
			CartItem itemToRemove = optionalCartItem.get();

			// Xóa CartItem khỏi giỏ hàng và cập nhật danh sách cartItems trong session
			shoppingCartService.remove(itemToRemove);
			cartItems.remove(itemToRemove);
		}

		// Cập nhật số lượng sản phẩm trong giỏ hàng và chuyển hướng đến trang thanh toán
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return "redirect:/checkout";
	}

	// Hiển thị trang thanh toán
	@GetMapping(value = "/checkout")
	public String checkOut(Model model, User user) {
		// Tạo mới một đối tượng Order và gửi đến view
		Order order = new Order();
		model.addAttribute("order", order);

		// Lấy danh sách sản phẩm trong giỏ hàng và tính tổng số tiền và số lượng sản phẩm
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", shoppingCartService.getAmount());
		model.addAttribute("NoOfItems", shoppingCartService.getCount());
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		// Gửi thông tin tổng giá trị sản phẩm và số lượng sản phẩm đến view
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		commomDataService.commonData(model, user);

		return "web/shoppingCart_checkout";
	}

	// Xử lý thanh toán khi submit form
	@PostMapping(value = "/checkout")
	@Transactional
	public String checkedOut(Model model, Order order, HttpServletRequest request, User user)
			throws MessagingException {

		// Lấy thông tin loại thanh toán từ form
		String checkOut = request.getParameter("checkOut");

		// Lấy danh sách sản phẩm trong giỏ hàng
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();

		// Tính tổng giá trị đơn hàng
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}

		// Copy thông tin order vào orderFinal để sử dụng sau này
		BeanUtils.copyProperties(order, orderFinal);

		// Nếu chọn thanh toán qua PayPal
		if (StringUtils.equals(checkOut, "paypal")) {

			// Tạo đường dẫn hủy và thành công cho thanh toán PayPal
			String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
			String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
			try {
				// Tính tổng giá trị cần thanh toán (đơn vị tiền tệ, phương thức thanh toán, mô tả, và các thông tin liên quan)
				totalPrice = totalPrice / 25456;
				Payment payment = paypalService.createPayment(totalPrice, "USD", PaypalPaymentMethod.paypal,
						PaypalPaymentIntent.sale, "payment description", cancelUrl, successUrl);
				for (Links links : payment.getLinks()) {
					if (links.getRel().equals("approval_url")) {
						return "redirect:" + links.getHref();
					}
				}
			} catch (PayPalRESTException e) {
				log.error(e.getMessage());
			}

		}

		// Lưu thông tin đơn hàng vào cơ sở dữ liệu
		session = request.getSession();
		Date date = new Date();
		order.setOrderDate(date);
		order.setStatus(0);
		order.getOrderId();
		order.setAmount(totalPrice);
		order.setUser(user);
		orderRepository.save(order);

		// Lưu thông tin chi tiết đơn hàng vào cơ sở dữ liệu
		for (CartItem cartItem : cartItems) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setOrder(order);
			orderDetail.setProduct(cartItem.getProduct());
			double unitPrice = cartItem.getProduct().getPrice();
			orderDetail.setPrice(unitPrice);
			orderDetailRepository.save(orderDetail);
		}

		// Gửi email xác nhận đơn hàng
		commomDataService.sendSimpleEmail(user.getEmail(), "Greenty-Shop Xác Nhận Đơn hàng", "xác nhận", cartItems,
				totalPrice, order);

		// Xóa giỏ hàng sau khi thanh toán thành công
		shoppingCartService.clear();
		session.removeAttribute("cartItems");
		model.addAttribute("orderId", order.getOrderId());

		// Chuyển hướng đến trang thanh toán thành công
		return "redirect:/checkout_success";
	}

	// Xử lý thanh toán qua PayPal thành công
	@GetMapping(URL_PAYPAL_SUCCESS)
	public String successPay(@RequestParam("" + "" + "") String paymentId, @RequestParam("PayerID") String payerId,
							 HttpServletRequest request, User user, Model model) throws MessagingException {
		// Lấy danh sách sản phẩm trong giỏ hàng
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", shoppingCartService.getAmount());

		// Tính tổng giá trị đơn hàng
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);
		}
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		try {
			// Thực hiện thanh toán PayPal
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if (payment.getState().equals("approved")) {

				// Lưu thông tin đơn hàng vào cơ sở dữ liệu
				session = request.getSession();
				Date date = new Date();
				orderFinal.setOrderDate(date);
				orderFinal.setStatus(0);
				orderFinal.getOrderId();
				orderFinal.setUser(user);
				orderFinal.setAmount(totalPrice);
				orderRepository.save(orderFinal);

				// Lưu thông tin chi tiết đơn hàng vào cơ sở dữ liệu
				for (CartItem cartItem : cartItems) {
					OrderDetail orderDetail = new OrderDetail();
					orderDetail.setQuantity(cartItem.getQuantity());
					orderDetail.setOrder(orderFinal);
					orderDetail.setProduct(cartItem.getProduct());
					double unitPrice = cartItem.getProduct().getPrice();
					orderDetail.setPrice(unitPrice);
					orderDetailRepository.save(orderDetail);
				}

				// Gửi email xác nhận đơn hàng
				commomDataService.sendSimpleEmail(user.getEmail(), "Greenty-Shop Xác Nhận Đơn hàng", "aaaa", cartItems,
						totalPrice, orderFinal);

				// Xóa giỏ hàng sau khi thanh toán thành công
				shoppingCartService.clear();
				session.removeAttribute("cartItems");
				model.addAttribute("orderId", orderFinal.getOrderId());

				// Reset orderFinal để chuẩn bị cho các đơn hàng tiếp theo
				orderFinal = new Order();
				return "redirect:/checkout_paypal_success";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}

		// Nếu không thành công, chuyển hướng về trang chủ
		return "redirect:/";
	}

	// Xử lý khi thanh toán bằng tiền mặt thành công
	@GetMapping(value = "/checkout_success")
	public String checkoutSuccess(Model model, User user) {
		// Gửi thông tin chung đến view
		commomDataService.commonData(model, user);

		// Chuyển hướng đến trang thanh toán thành công
		return "web/checkout_success";
	}

	// Xử lý khi thanh toán qua PayPal thành công
	@GetMapping(value = "/checkout_paypal_success")
	public String paypalSuccess(Model model, User user) {
		// Gửi thông tin chung đến view
		commomDataService.commonData(model, user);

		// Chuyển hướng đến trang thanh toán qua PayPal thành công
		return "web/checkout_paypal_success";
	}

	// Cập nhật số lượng sản phẩm trong giỏ hàng
	@PutMapping(value = "/updateQuantity", params = { "productId", "quantity" })
	public String updateQ(ModelMap model, HttpSession session, @RequestParam("productId") Long id,
						  @RequestParam("quantity") int qty) {
		shoppingCartService.updateQuantity(id, qty);;

		// Chuyển hướng đến trang giỏ hàng
		return "web/shoppingCart_checkout";
	}
}
