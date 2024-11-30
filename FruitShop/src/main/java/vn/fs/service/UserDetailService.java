package vn.fs.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.fs.entities.Role;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;  // Inject UserRepository để tương tác với cơ sở dữ liệu

	// Phương thức này được gọi bởi Spring Security để tìm kiếm người dùng trong cơ sở dữ liệu
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);  // Tìm người dùng bằng địa chỉ email

		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");  // Nếu không tìm thấy, ném ngoại lệ
		}

		if (!user.getStatus()) {
			throw new DisabledException("Tài khoản của bạn đã bị khóa.");  // Nếu tài khoản bị khóa, ném ngoại lệ
		}

		// Trả về UserDetails là một đối tượng của Spring Security User với thông tin người dùng từ cơ sở dữ liệu
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),  // Email
				user.getPassword(),  // Mật khẩu
				mapRolesToAuthorities(user.getRoles())  // Quyền hạn được gán từ vai trò của người dùng
		);
	}

	// Phương thức này ánh xạ các vai trò của người dùng thành danh sách các quyền hạn (Authorities)
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))  // Chuyển đổi từ Role sang GrantedAuthority
				.collect(Collectors.toList());  // Thu thập thành danh sách
	}
}
