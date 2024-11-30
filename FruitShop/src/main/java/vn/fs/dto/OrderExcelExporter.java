package vn.fs.dto;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Data;
import vn.fs.entities.Order;

@Data
public class OrderExcelExporter {

	private XSSFWorkbook workbook; // Workbook Excel để lưu trữ dữ liệu
	private XSSFSheet sheet; // Sheet trong Workbook để lưu trữ dữ liệu

	private List<Order> listOrDetails; // Danh sách đơn hàng để export

	public OrderExcelExporter(List<Order> listOrDetails) {
		this.listOrDetails = listOrDetails;
		workbook = new XSSFWorkbook(); // Khởi tạo Workbook Excel mới
		sheet = workbook.createSheet("OrderDetails"); // Tạo một sheet mới có tên là "OrderDetails"
	}

	private void writeHeaderRow() {
		// Tạo header row (dòng tiêu đề) trong Excel
		Row row = sheet.createRow(0); // Tạo một dòng mới (dòng đầu tiên - index 0)

		// Tạo các ô (cell) cho từng cột trong header
		Cell cell = row.createCell(0);
		cell.setCellValue("Mã đơn hàng");

		cell = row.createCell(1);
		cell.setCellValue("Tên khách hàng");

		cell = row.createCell(2);
		cell.setCellValue("Số điện thoại");

		cell = row.createCell(3);
		cell.setCellValue("Địa chỉ");

		cell = row.createCell(4);
		cell.setCellValue("Email");

		cell = row.createCell(5);
		cell.setCellValue("Ngày đặt hàng");

		cell = row.createCell(6);
		cell.setCellValue("Tổng tiền");
	}

	private void writeDataRows() {
		int rowCount = 1; // Bắt đầu từ dòng số 1 (vì dòng 0 đã được sử dụng cho header)

		// Viết dữ liệu từ danh sách đơn hàng vào Excel
		for (Order order : listOrDetails) {
			Row row = sheet.createRow(rowCount++); // Tạo một dòng mới cho mỗi đơn hàng

			Date orderDate = order.getOrderDate(); // Lấy ngày đặt hàng từ đối tượng Order

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng ngày tháng

			String formattedDate = dateFormat.format(orderDate); // Định dạng ngày tháng thành chuỗi

			// Viết dữ liệu từ đối tượng Order vào từng ô (cell) tương ứng trong dòng
			Cell cell = row.createCell(0);
			cell.setCellValue(order.getOrderId());

			cell = row.createCell(1);
			cell.setCellValue(order.getUser().getName());  // Lấy tên khách hàng từ đối tượng User trong Order

			cell = row.createCell(2);
			cell.setCellValue(order.getPhone()); // Lấy số điện thoại từ đối tượng Order

			cell = row.createCell(3);
			cell.setCellValue(order.getAddress()); // Lấy địa chỉ từ đối tượng Order

			cell = row.createCell(4);
			cell.setCellValue(order.getUser().getEmail()); // Lấy email khách hàng từ đối tượng User trong Order

			cell = row.createCell(5);
			cell.setCellValue(formattedDate); // Lấy ngày đặt hàng đã được định dạng từ đối tượng Order

			cell = row.createCell(6);
			cell.setCellValue(order.getAmount()); // Lấy tổng tiền từ đối tượng Order
		}
	}

	public void export(HttpServletResponse response) throws IOException {
		// Gọi các phương thức để viết header và dữ liệu vào Excel
		writeHeaderRow();
		writeDataRows();

		// Lấy đối tượng outputStream để ghi dữ liệu xuống response
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream); // Ghi workbook xuống outputStream
		workbook.close(); // Đóng workbook sau khi ghi xong
		outputStream.close(); // Đóng outputStream
	}
}
