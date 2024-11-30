package vn.fs.util;

import javax.servlet.http.HttpServletRequest;

public class Utils {

	// Phương thức lấy URL cơ sở của ứng dụng web từ HttpServletRequest
	public static String getBaseURL(HttpServletRequest request) {
		String scheme = request.getScheme();  // Lấy scheme của URL (http hoặc https)
		String serverName = request.getServerName();  // Lấy tên máy chủ
		int serverPort = request.getServerPort();  // Lấy cổng của máy chủ

		String contextPath = request.getContextPath();  // Lấy context path của ứng dụng web

		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);  // Bắt đầu xây dựng URL với scheme và tên máy chủ

		// Nếu cổng không phải là 80 hoặc 443, thêm vào URL
		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);  // Thêm context path vào URL

		// Nếu URL kết thúc bằng "/", thêm vào một lần nữa để tránh URL như "http://example.com//"
		if (url.toString().endsWith("/")) {
			url.append("/");
		}

		return url.toString();  // Trả về URL cơ sở đã xây dựng
	}

}
