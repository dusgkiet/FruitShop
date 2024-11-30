package vn.fs.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.dto.ChangePassword;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;

@Controller
public class AccountController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	// Xử lý yêu cầu GET tới "/forgotPassword"
	@GetMapping(value = "/forgotPassword")
	public String forgotPassword() {
		return "web/forgotPassword"; // Trả về trang forgotPassword để người dùng nhập email cần reset mật khẩu
	}

	// Xử lý yêu cầu POST từ form "/forgotPassword"
	@PostMapping("/forgotPassword")
	public ModelAndView forgotPassword(ModelMap model, @RequestParam("email") String email) {
		List<User> listUser = userRepository.findAll();
		for (User user : listUser) {
			if (email.trim().equals(user.getEmail())) {
				// Tạo mã OTP ngẫu nhiên
				session.removeAttribute("otp");
				int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
				session.setAttribute("otp", random_otp);

				// Gửi email chứa mã OTP đến người dùng
				String body = "<div>\r\n" + "<h3>Mã xác thực OTP của bạn là: <span style=\"color:#119744; font-weight: bold;\">"
						+ random_otp + "</span>. Vui lòng không cung cấp mã OTP cho bất kì ai để bảo vệ tài khoản của bạn. </h3>\r\n" + "</div>";
				sendMailService.queue(email, "Quên mật khẩu?", body);

				// Chuẩn bị model và chuyển hướng đến trang xác nhận OTP
				model.addAttribute("email", email);
				model.addAttribute("message", "Mã xác thực OTP đã được gửi tới Email : " + user.getEmail() + " , hãy kiểm tra Email của bạn!");
				return new ModelAndView("/web/confirmOtpForgotPassword", model);
			}
		}
		// Nếu email không tồn tại trong hệ thống
		model.addAttribute("error", "Email này chưa đăng ký!");
		return new ModelAndView("web/forgotPassword", model);
	}

	// Xử lý yêu cầu POST từ form "/confirmOtpForgotPassword"
	@PostMapping("/confirmOtpForgotPassword")
	public ModelAndView confirm(ModelMap model, @RequestParam("otp") String otp, @RequestParam("email") String email) {
		// Kiểm tra mã OTP nhập vào có trùng khớp với mã OTP lưu trong session hay không
		if (otp.equals(String.valueOf(session.getAttribute("otp")))) {
			// Chuẩn bị model và chuyển hướng đến trang đổi mật khẩu mới
			model.addAttribute("email", email);
			model.addAttribute("newPassword", "");
			model.addAttribute("confirmPassword", "");
			model.addAttribute("changePassword", new ChangePassword());
			return new ModelAndView("web/changePassword", model);
		}
		// Nếu mã OTP không đúng
		model.addAttribute("error", "Mã xác thực OTP không đúng, vui lòng thử lại!");
		return new ModelAndView("web/confirmOtpForgotPassword", model);
	}

	// Xử lý yêu cầu POST từ form "/changePassword"
	@PostMapping("/changePassword")
	public ModelAndView changePassword(ModelMap model,
									   @Valid @ModelAttribute("changePassword") ChangePassword changePassword, BindingResult result,
									   @RequestParam("email") String email, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword) {

		// Nếu có lỗi xảy ra khi validate form
		if (result.hasErrors()) {
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("confirmPassword", confirmPassword);
			model.addAttribute("email", email);
			return new ModelAndView("/web/changePassword", model);
		}

		// Nếu mật khẩu mới và xác nhận mật khẩu không khớp nhau
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Mật khẩu mới không khớp, vui lòng thử lại!");
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("confirmPassword", confirmPassword);
			model.addAttribute("email", email);
			return new ModelAndView("/web/changePassword", model);
		}

		// Tìm người dùng theo email và cập nhật mật khẩu mới đã được mã hóa
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.setStatus(true); // Kích hoạt tài khoản (nếu cần)
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			model.addAttribute("message", "Đặt lại mật khẩu thành công!");
		} else {
			model.addAttribute("error", "Đã có lỗi xảy ra khi đặt lại mật khẩu!");
		}

		session.removeAttribute("otp"); // Xóa mã OTP sau khi đã sử dụng
		model.addAttribute("email", ""); // Xóa email trong model để tránh hiển thị lên form
		return new ModelAndView("/web/changePassword", model);
	}
}
