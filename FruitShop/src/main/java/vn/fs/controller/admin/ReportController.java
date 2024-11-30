package vn.fs.controller.admin;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.fs.entities.OrderDetail;
import vn.fs.entities.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.UserRepository;

@Controller
public class ReportController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	// Thống kê sản phẩm được bán
	@GetMapping(value = "/admin/reports")
	public String report(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.repo();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}

	// Thống kê sản phẩm theo danh mục
	@RequestMapping(value = "/admin/reportCategory")
	public String reportcategory(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereCategory();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}

	// Thống kê sản phẩm theo năm
	@RequestMapping(value = "/admin/reportYear")
	public String reportyear(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereYear();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}

	// Thống kê sản phẩm theo tháng
	@RequestMapping(value = "/admin/reportMonth")
	public String reportmonth(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereMonth();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}

	// Thống kê sản phẩm theo quý
	@RequestMapping(value = "/admin/reportQuarter")
	public String reportquarter(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.repoWhereQUARTER();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}

	// Thống kê sản phẩm theo khách hàng
	@RequestMapping(value = "/admin/reportOrderCustomer")
	public String reportordercustomer(Model model, Principal principal) throws SQLException {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);

		OrderDetail orderDetail = new OrderDetail();
		model.addAttribute("orderDetail", orderDetail);

		// Lấy danh sách báo cáo thông qua các phương thức từ repository
		List<Object[]> listReportCommon = orderDetailRepository.reportCustommer();
		model.addAttribute("listReportCommon", listReportCommon);

		return "admin/statistical"; // Trả về view "statistical" để hiển thị dữ liệu thống kê
	}
}
