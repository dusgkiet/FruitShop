package vn.fs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import vn.fs.config.PaypalPaymentIntent;
import vn.fs.config.PaypalPaymentMethod;

@Service
public class PaypalService {

	@Autowired
	private APIContext apiContext;  // Đối tượng APIContext được sử dụng để gọi PayPal API

	// Phương thức tạo thanh toán PayPal
	public Payment createPayment(
			Double total,
			String currency,
			PaypalPaymentMethod method,
			PaypalPaymentIntent intent,
			String description,
			String cancelUrl,
			String successUrl) throws PayPalRESTException {

		// Tạo đối tượng Amount đại diện cho số tiền thanh toán
		Amount amount = new Amount();
		amount.setCurrency(currency);  // Đặt loại tiền tệ
		amount.setTotal(String.format("%.2f", total));  // Đặt tổng số tiền

		// Tạo đối tượng Transaction đại diện cho giao dịch thanh toán
		Transaction transaction = new Transaction();
		transaction.setDescription(description);  // Đặt mô tả giao dịch
		transaction.setAmount(amount);  // Đặt số tiền thanh toán

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);  // Thêm giao dịch vào danh sách giao dịch

		// Tạo đối tượng Payer đại diện cho người thanh toán
		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());  // Đặt phương thức thanh toán

		// Tạo đối tượng Payment đại diện cho thông tin thanh toán
		Payment payment = new Payment();
		payment.setIntent(intent.toString());  // Đặt mục đích thanh toán
		payment.setPayer(payer);  // Đặt người thanh toán
		payment.setTransactions(transactions);  // Đặt danh sách giao dịch

		// Tạo đối tượng RedirectUrls đại diện cho các URL dẫn hướng khi thanh toán hoàn thành hoặc hủy bỏ
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);  // Đặt URL hủy bỏ
		redirectUrls.setReturnUrl(successUrl);  // Đặt URL khi thành công

		payment.setRedirectUrls(redirectUrls);  // Đặt RedirectUrls cho Payment
		apiContext.setMaskRequestId(true);  // Đặt cờ để ẩn yêu cầu ID

		return payment.create(apiContext);  // Gọi API để tạo thanh toán và trả về đối tượng Payment
	}

	// Phương thức thực hiện thanh toán PayPal
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		Payment payment = new Payment();
		payment.setId(paymentId);  // Đặt ID thanh toán cần thực hiện

		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);  // Đặt ID người thanh toán

		return payment.execute(apiContext, paymentExecute);  // Gọi API để thực hiện thanh toán và trả về đối tượng Payment
	}
}
