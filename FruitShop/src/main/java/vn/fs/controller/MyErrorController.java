package vn.fs.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyErrorController implements ErrorController {

	// Xử lý khi có lỗi xảy ra, ví dụ như trang không tồn tại (404)
	@RequestMapping("/error")
	public String handleError() {
		// Thực hiện các hành động như ghi log
		// Sau đó chuyển hướng đến trang web/notFound để hiển thị thông báo lỗi
		return "web/notFound";
	}

	// Phương thức này được yêu cầu bởi interface ErrorController
	// Nhưng vì chúng ta không cần xử lý logic nào khác ngoài phương thức handleError
	// Nên phương thức này chỉ đơn giản trả về null

	public String getErrorPath() {
		return null;
	}
}
