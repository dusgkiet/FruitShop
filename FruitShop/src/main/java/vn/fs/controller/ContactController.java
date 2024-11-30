package vn.fs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.fs.commom.CommomDataService;
import vn.fs.entities.User;

@Controller
public class ContactController extends CommomController {

	@Autowired
	CommomDataService commomDataService;

	// Mapping cho đường dẫn /contact
	@GetMapping(value = "/contact")
	public String contact(Model model, User user) {

		// Gọi phương thức commonData từ CommomDataService để cung cấp dữ liệu chung cho view
		commomDataService.commonData(model, user);

		// Trả về tên của view, ở đây là "web/contact"
		return "web/contact";
	}
}
