package vn.fs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.fs.dto.MailInfo;
import vn.fs.service.SendMailService;

@Service
public class SendMailServiceImplement implements SendMailService {
	@Autowired
	JavaMailSender sender;

	List<MailInfo> list = new ArrayList<>();

	// Phương thức gửi email
	@Override
	public void send(MailInfo mail) throws MessagingException, IOException {
		// Tạo message MimeMessage
		MimeMessage message = sender.createMimeMessage();
		// Sử dụng MimeMessageHelper để thiết lập thông tin chi tiết cho message
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom(mail.getFrom());  // Thiết lập người gửi
		helper.setTo(mail.getTo());  // Thiết lập người nhận
		helper.setSubject(mail.getSubject());  // Thiết lập tiêu đề
		helper.setText(mail.getBody(), true);  // Thiết lập nội dung email, hỗ trợ HTML

		// Nếu có đính kèm file
		if (mail.getAttachments() != null) {
			FileSystemResource file = new FileSystemResource(new File(mail.getAttachments()));
			helper.addAttachment(mail.getAttachments(), file);  // Đính kèm file vào email
		}

		// Gửi email thông qua sender (JavaMailSender)
		sender.send(message);
	}

	// Phương thức đưa email vào hàng đợi để gửi sau
	@Override
	public void queue(MailInfo mail) {
		list.add(mail);  // Thêm email vào danh sách hàng đợi
	}

	// Phương thức đưa email vào hàng đợi với thông tin cơ bản
	@Override
	public void queue(String to, String subject, String body) {
		queue(new MailInfo(to, subject, body));  // Đưa email vào hàng đợi với thông tin cơ bản
	}

	// Phương thức chạy định kỳ để gửi các email trong hàng đợi
	@Override
	@Scheduled(fixedDelay = 5000)  // Chạy định kỳ sau mỗi 5000ms (5 giây)
	public void run() {
		while (!list.isEmpty()) {  // Lặp cho đến khi danh sách hàng đợi không còn trống
			MailInfo mail = list.remove(0);  // Lấy và loại bỏ email đầu tiên từ danh sách hàng đợi
			try {
				this.send(mail);  // Gửi email
			} catch (Exception e) {
				e.printStackTrace();  // Xử lý nếu gặp lỗi khi gửi email
			}
		}
	}
}
