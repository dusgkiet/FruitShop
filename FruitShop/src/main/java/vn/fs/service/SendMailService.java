package vn.fs.service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import vn.fs.dto.MailInfo;

@Service
public interface SendMailService {

	// Phương thức để thực thi việc gửi email theo hàng đợi
	void run();

	// Phương thức để đưa email vào hàng đợi với các thông tin cơ bản
	void queue(String to, String subject, String body);

	// Phương thức để đưa một đối tượng MailInfo vào hàng đợi
	void queue(MailInfo mail);

	// Phương thức để gửi email
	void send(MailInfo mail) throws MessagingException, IOException;
}
