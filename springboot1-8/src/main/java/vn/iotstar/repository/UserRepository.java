package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Phục vụ chức năng Đăng nhập và Quên mật khẩu cơ bản
    Optional<User> findByEmailIgnoreCase(String email);
    
    // Kiểm tra email đã tồn tại chưa (phục vụ chức năng Đăng ký)
    boolean existsByEmailIgnoreCase(String email);
    
    // HÀM CÒN THIẾU: Tìm kiếm User và nạp luôn thông tin Role (Tránh lỗi Lazy Loading)
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);
}