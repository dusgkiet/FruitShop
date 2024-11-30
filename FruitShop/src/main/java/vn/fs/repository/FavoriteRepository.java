package vn.fs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fs.entities.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	// Lấy dòng dữ liệu từ bảng favorites với điều kiện product_id và user_id
	@Query(value = "SELECT * FROM favorites where product_id  = ? and user_id = ?;", nativeQuery = true)
	public Favorite selectSaves(Long productId, Long userId);

	// Lấy danh sách các dòng dữ liệu từ bảng favorites của một user
	@Query(value = "SELECT * FROM favorites where user_id = ?;", nativeQuery = true)
	public List<Favorite> selectAllSaves(Long userId);

	// Đếm số lượng dòng dữ liệu từ bảng favorites của một user
	@Query(value = "SELECT Count(favorite_id)  FROM favorites  where user_id = ?;", nativeQuery = true)
	public Integer selectCountSave(Long userId);

}
