package vn.fs.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class SuccessHandler implements AuthenticationSuccessHandler {
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
										Authentication authentication) throws IOException, ServletException {
		// Kiểm tra xem người dùng có vai trò ROLE_USER không
		boolean hasRoleUser = false;
		// Kiểm tra xem người dùng có vai trò ROLE_ADMIN không
		boolean hasAdmin = false;
		// Kiểm tra xem người dùng có vai trò ROLE_EMPLOYEE không
		boolean hasEmployee = false;

		// Lấy danh sách các quyền (authorities) của người dùng từ Authentication object
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		// Duyệt qua từng quyền của người dùng để kiểm tra vai trò
		for (GrantedAuthority grantedAuthority : authorities) {
			// Nếu người dùng có vai trò ROLE_USER
			if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
				hasRoleUser = true;
				break;
			}
			// Nếu người dùng có vai trò ROLE_ADMIN
			else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				hasAdmin = true;
				break;
			}
			// Nếu người dùng có vai trò ROLE_EMPLOYEE
			else if (grantedAuthority.getAuthority().equals("ROLE_EMPLOYEE")) {
				hasEmployee = true;
				break;
			}
		}

		// Nếu người dùng có vai trò ROLE_USER, điều hướng đến "/checkout"
		if (hasRoleUser) {
			redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/checkout");
		}
		// Nếu người dùng có vai trò ROLE_ADMIN hoặc ROLE_EMPLOYEE, điều hướng đến "/admin/home"
		else if (hasAdmin || hasEmployee) {
			redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/admin/home");
		}
		// Nếu không có vai trò nào khớp, ném ra một trạng thái không hợp lệ (IllegalStateException)
		else {
			throw new IllegalStateException();
		}
	}
}
