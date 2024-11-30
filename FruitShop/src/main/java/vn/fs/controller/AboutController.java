package vn.fs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.User;

@Controller
public class AboutController extends CommomController {

	@Autowired
	CommomDataService commomDataService;

	// Xử lý yêu cầu GET tới "/aboutUs"
	@GetMapping(value = "/aboutUs")
	public String about(Model model, User user) {
		// Gọi dịch vụ chung để thêm dữ liệu chung vào model
		commomDataService.commonData(model, user);
		return "web/about"; // Trả về tên view logic là "web/about"
	}
}
