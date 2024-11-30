package vn.fs.config;

// Định nghĩa một enum để đại diện cho các kiểu intent của thanh toán PayPal
public enum PaypalPaymentIntent {
	sale,      // Bán hàng trực tiếp, khi giao dịch được thực hiện ngay lập tức
	authorize, // Ủy quyền, khi giao dịch được ủy quyền nhưng chưa thực hiện
	order      // Đặt hàng, khi giao dịch được đặt hàng nhưng chưa thực hiện
}
