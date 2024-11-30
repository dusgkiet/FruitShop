package vn.fs.dto;

import lombok.AllArgsConstructor; // Import annotation Lombok để tự động tạo constructor có tham số
import lombok.Data; // Import annotation Lombok để tự động tạo getter và setter
import lombok.NoArgsConstructor; // Import annotation Lombok để tự động tạo constructor mặc định

@Data // Annotation Lombok để tự động tạo getter, setter, toString, equals, hashCode
@AllArgsConstructor // Annotation Lombok để tự động tạo constructor có tham số
@NoArgsConstructor // Annotation Lombok để tự động tạo constructor mặc định
public class MailInfo {
	private String from; // Địa chỉ email người gửi
	private String to; // Địa chỉ email người nhận
	private String subject; // Chủ đề email
	private String body; // Nội dung email
	private String attachments; // Danh sách các file đính kèm (nếu có)

	// Constructor có tham số để khởi tạo từ địa chỉ người nhận, chủ đề và nội dung email
	public MailInfo(String to, String subject, String body) {
		this.from = "Greenty Shop <support@gmail.com>"; // Đặt người gửi mặc định
		this.to = to; // Thiết lập địa chỉ người nhận
		this.subject = subject; // Thiết lập chủ đề email
		this.body = body; // Thiết lập nội dung email
	}
}
