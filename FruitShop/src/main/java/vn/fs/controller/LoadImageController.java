package vn.fs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LoadImageController {

	@Value("${upload.path}")
	private String pathUploadImage;

	// Endpoint để tải hình ảnh từ server
	@GetMapping(value = "loadImage")
	@ResponseBody
	public byte[] index(@RequestParam(value = "imageName") String imageName, HttpServletResponse response)
			throws IOException {
		response.setContentType("image/jpeg"); // Thiết lập kiểu content là hình JPEG

		// Tạo đối tượng File từ đường dẫn và tên hình ảnh được truyền vào
		File file = new File(pathUploadImage + File.separatorChar + imageName);
		InputStream inputStream = null;

		// Nếu file tồn tại, tiến hành đọc hình ảnh
		if (file.exists()) {
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			// Nếu inputStream không null, chuyển đổi thành mảng byte và trả về
			if (inputStream != null) {
				return IOUtils.toByteArray(inputStream);
			}
		}
		return null; // Trả về null nếu không tìm thấy hoặc có lỗi xảy ra
	}

}
