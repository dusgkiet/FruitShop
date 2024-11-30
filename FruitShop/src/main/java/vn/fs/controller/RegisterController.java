package vn.fs.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.entities.Role;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;

@Controller
public class RegisterController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	HttpSession session;

	// Hiển thị form đăng ký
	@GetMapping("/register")
	public ModelAndView registerForm(ModelMap model) {
		model.addAttribute("user", new User());
		return new ModelAndView("web/register", model);
	}

	// Xử lý đăng ký tài khoản
	@PostMapping("/register")
	public String register(ModelMap model, @Validated @ModelAttribute("user") User dto, BindingResult result,
						   @RequestParam("password") String password) {
		// Nếu có lỗi trong việc nhập liệu
		if (result.hasErrors()) {
			return "web/register";
		}
		// Kiểm tra email đã được sử dụng chưa
		if (!checkEmail(dto.getEmail())) {
			model.addAttribute("error", "Email này đã được sử dụng!");
			return "web/register";
		}
		// Xóa session cũ và tạo mã OTP ngẫu nhiên
		session.removeAttribute("otp");
		int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
		session.setAttribute("otp", random_otp);
		// Chuẩn bị nội dung email chứa mã OTP
		String body = "<div>\r\n" + "<h3>Mã xác thực OTP của bạn là: <span style=\"color:#119744; font-weight: bold;\">"
				+ random_otp + "</span></h3>\r\n" + "</div>";
		// Gửi email chứa mã OTP
		sendMailService.queue(dto.getEmail(), "Đăng kí tài khoản", body);

		model.addAttribute("user", dto);
		model.addAttribute("message", "Mã xác thực OTP đã được gửi tới Email : " + dto.getEmail()
				+ " , hãy kiểm tra Email của bạn!");

		return "/web/confirmOtpRegister";
	}

	// Xác nhận đăng ký với mã OTP
	@PostMapping("/confirmOtpRegister")
	public ModelAndView confirmRegister(ModelMap model, @ModelAttribute("user") User dto,
										@RequestParam("password") String password, @RequestParam("otp") String otp) {
		// Nếu mã OTP nhập vào khớp với mã OTP đã gửi
		if (otp.equals(String.valueOf(session.getAttribute("otp")))) {
			// Mã hóa mật khẩu và cài đặt thông tin người dùng mới
			dto.setPassword(bCryptPasswordEncoder.encode(password));
			dto.setRegisterDate(new Date());
			dto.setStatus(true);
			dto.setAvatar("user.png");
			dto.setRoles(Arrays.asList(new Role("ROLE_USER")));
			// Lưu thông tin người dùng vào cơ sở dữ liệu
			userRepository.save(dto);

			// Xóa mã OTP khỏi session và thông báo đăng ký thành công
			session.removeAttribute("otp");
			model.addAttribute("message", "Đăng kí thành công");
			return new ModelAndView("web/login");
		}

		// Nếu mã OTP không khớp, thông báo lỗi và yêu cầu nhập lại
		model.addAttribute("user", dto);
		model.addAttribute("error", "Mã xác thực OTP không chính xác, hãy thử lại!");
		return new ModelAndView("web/confirmOtpRegister", model);
	}

	// Kiểm tra email đã tồn tại trong cơ sở dữ liệu
	public boolean checkEmail(String email) {
		List<User> list = userRepository.findAll();
		for (User c : list) {
			if (c.getEmail().equalsIgnoreCase(email)) {
				return false;
			}
		}
		return true;
	}

}
