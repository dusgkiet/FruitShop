package vn.fs.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring
public class PaypalConfig {

	@Value("${paypal.client.app}")
	private String clientId; // Tiêm giá trị từ file cấu hình vào biến clientId

	@Value("${paypal.client.secret}")
	private String clientSecret; // Tiêm giá trị từ file cấu hình vào biến clientSecret

	@Value("${paypal.mode}")
	private String mode; // Tiêm giá trị từ file cấu hình vào biến mode

	// Định nghĩa bean Map chứa cấu hình SDK của PayPal
	@Bean
	public Map<String, String> paypalSdkConfig() {
		Map<String, String> sdkConfig = new HashMap<>(); // Tạo một HashMap để chứa cấu hình
		sdkConfig.put("mode", mode); // Đặt giá trị cho khóa "mode" bằng biến mode
		return sdkConfig; // Trả về Map cấu hình
	}

	// Định nghĩa bean OAuthTokenCredential để lấy token xác thực từ PayPal
	@Bean
	public OAuthTokenCredential authTokenCredential() {
		return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig()); // Tạo OAuthTokenCredential với clientId, clientSecret, và cấu hình SDK
	}

	// Định nghĩa bean APIContext để tương tác với API của PayPal
	@Bean
	public APIContext apiContext() throws PayPalRESTException {
		APIContext apiContext = new APIContext(authTokenCredential().getAccessToken()); // Tạo APIContext với token xác thực
		apiContext.setConfigurationMap(paypalSdkConfig()); // Đặt cấu hình SDK cho APIContext
		return apiContext; // Trả về APIContext
	}
}
