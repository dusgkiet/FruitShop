package vn.fs.dto;

import javax.validation.constraints.NotEmpty; // Import annotation để kiểm tra không được để trống
import org.hibernate.validator.constraints.Length; // Import annotation để kiểm tra độ dài của chuỗi

import lombok.AllArgsConstructor; // Import annotation Lombok để tự động tạo constructor có tham số
import lombok.Data; // Import annotation Lombok để tự động tạo getter và setter
import lombok.NoArgsConstructor; // Import annotation Lombok để tự động tạo constructor mặc định

@Data // Annotation Lombok để tự động tạo getter và setter
@AllArgsConstructor // Annotation Lombok để tự động tạo constructor có tham số
@NoArgsConstructor // Annotation Lombok để tự động tạo constructor mặc định
public class ChangePassword {

	@NotEmpty // Đánh dấu trường newPassword không được để trống
	@Length(min = 6) // Đánh dấu độ dài tối thiểu của newPassword là 6 ký tự
	private String newPassword; // Trường dữ liệu mới cho mật khẩu

	@NotEmpty // Đánh dấu trường confirmPassword không được để trống
	@Length(min = 6) // Đánh dấu độ dài tối thiểu của confirmPassword là 6 ký tự
	private String confirmPassword; // Trường xác nhận lại mật khẩu mới
}
