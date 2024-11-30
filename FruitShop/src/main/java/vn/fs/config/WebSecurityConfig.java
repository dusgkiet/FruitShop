package vn.fs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vn.fs.service.UserDetailService;

@Configuration // Đánh dấu lớp này là một lớp cấu hình
@EnableWebSecurity // Kích hoạt bảo mật web cho ứng dụng
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired // Tiêm UserDetailService đã được định nghĩa trước đó
	private UserDetailService userDetailService;

	// Định nghĩa Bean cho BCryptPasswordEncoder để mã hóa mật khẩu
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Định nghĩa Bean cho DaoAuthenticationProvider
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userDetailService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}

	// Cấu hình global AuthenticationManagerBuilder để sử dụng userDetailService và passwordEncoder
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
	}

	// Cấu hình AuthenticationManagerBuilder để sử dụng DaoAuthenticationProvider
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	// Cấu hình bảo mật HTTP
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable(); // Tắt bảo vệ CSRF

		// Cấu hình trang dành cho admin
		http.authorizeRequests().antMatchers("/admin/users").access("hasRole('ROLE_ADMIN')");

		// Cho phép cả admin và employee truy cập vào các trang có URL bắt đầu bằng /admin/**
		http.authorizeRequests().antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE')");

		// Nếu chưa đăng nhập, sẽ chuyển hướng đến trang /login
		http.authorizeRequests().antMatchers("/checkout").access("hasRole('ROLE_USER')");

		// Cho phép tất cả mọi người truy cập vào các URL khác
		http.authorizeRequests()
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin() // Cấu hình trang đăng nhập
				.loginProcessingUrl("/doLogin")
				.loginPage("/login")
				.defaultSuccessUrl("/?login_success")
				.successHandler(new SuccessHandler()) // Xử lý thành công
				.failureUrl("/login?error=true&accountLocked=true") // Chuyển hướng nếu đăng nhập thất bại
				.failureUrl("/login?error=true")
				.permitAll()
				.and()
				.logout() // Cấu hình logout
				.invalidateHttpSession(true) // Vô hiệu hóa session
				.clearAuthentication(true) // Xóa xác thực
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/?logout_success") // Chuyển hướng khi logout thành công
				.permitAll();

		// Cấu hình remember-me
		http.rememberMe()
				.rememberMeParameter("remember");

		// Cấu hình trang lỗi khi truy cập bị từ chối
		http.exceptionHandling()
				.accessDeniedPage("/web/notFound");

	}
}
