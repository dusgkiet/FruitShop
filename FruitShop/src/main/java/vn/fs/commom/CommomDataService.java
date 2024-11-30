package vn.fs.commom;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import vn.fs.entities.CartItem;
import vn.fs.entities.Order;
import vn.fs.entities.User;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.ShoppingCartService;

@Service // Chỉ định rằng lớp này là một service và sẽ được quản lý bởi Spring
public class CommomDataService {

	@Autowired // Tiêm dependency của FavoriteRepository vào biến này
	FavoriteRepository favoriteRepository;

	@Autowired // Tiêm dependency của ShoppingCartService vào biến này
	ShoppingCartService shoppingCartService;

	@Autowired // Tiêm dependency của ProductRepository vào biến này
	ProductRepository productRepository;

	@Autowired // Tiêm dependency của JavaMailSender vào biến này
	public JavaMailSender emailSender;

	@Autowired // Tiêm dependency của TemplateEngine vào biến này
	TemplateEngine templateEngine;

	// Phương thức để lấy dữ liệu chung và gán vào model
	public void commonData(Model model, User user) {
		listCategoryByProductName(model); // Gọi phương thức để lấy danh sách sản phẩm theo danh mục
		Integer totalSave = 0;

		if (user != null) { // Kiểm tra xem người dùng có null hay không
			totalSave = favoriteRepository.selectCountSave(user.getUserId()); // Lấy số lượng sản phẩm yêu thích của người dùng
		}

		Integer totalCartItems = shoppingCartService.getCount(); // Lấy số lượng sản phẩm trong giỏ hàng

		model.addAttribute("totalSave", totalSave); // Gán số lượng sản phẩm yêu thích vào model
		model.addAttribute("totalCartItems", totalCartItems); // Gán số lượng sản phẩm trong giỏ hàng vào model

		Collection<CartItem> cartItems = shoppingCartService.getCartItems(); // Lấy danh sách sản phẩm trong giỏ hàng
		model.addAttribute("cartItems", cartItems); // Gán danh sách sản phẩm trong giỏ hàng vào model
	}

	// Đếm số lượng sản phẩm theo danh mục và gán vào model
	public void listCategoryByProductName(Model model) {
		List<Object[]> coutnProductByCategory = productRepository.listCategoryByProductName(); // Lấy danh sách số lượng sản phẩm theo danh mục
		model.addAttribute("coutnProductByCategory", coutnProductByCategory); // Gán danh sách số lượng sản phẩm theo danh mục vào model
	}

	// Gửi email đơn giản khi đơn hàng thành công
	public void sendSimpleEmail(String email, String subject, String contentEmail, Collection<CartItem> cartItems,
								double totalPrice, Order orderFinal) throws MessagingException {
		Locale locale = LocaleContextHolder.getLocale(); // Lấy locale hiện tại của ứng dụng

		Context ctx = new Context(locale); // Tạo ngữ cảnh (context) với locale hiện tại
		ctx.setVariable("cartItems", cartItems); // Đặt biến 'cartItems' vào context
		ctx.setVariable("totalPrice", totalPrice); // Đặt biến 'totalPrice' vào context
		ctx.setVariable("orderFinal", orderFinal); // Đặt biến 'orderFinal' vào context

		MimeMessage mimeMessage = emailSender.createMimeMessage(); // Tạo một đối tượng MimeMessage
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8"); // Tạo một MimeMessageHelper để giúp xây dựng email
		mimeMessageHelper.setSubject(subject); // Đặt tiêu đề email
		mimeMessageHelper.setTo(email); // Đặt địa chỉ email người nhận

		String htmlContent = templateEngine.process("mail/email_en.html", ctx); // Tạo nội dung HTML cho email bằng cách xử lý template 'email_en.html' với context
		mimeMessageHelper.setText(htmlContent, true); // Đặt nội dung email là HTML

		emailSender.send(mimeMessage); // Gửi email
	}
}
