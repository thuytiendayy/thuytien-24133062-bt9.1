package vn.iotstar.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private Integer categoryId;

    @Column(name = "CategoryName", columnDefinition = "NVARCHAR(255) NOT NULL")
    private String categoryname;

    @Column(name = "Images", columnDefinition = "NVARCHAR(500) NULL")
    private String images;

    @Column(name = "Status")
    private int status = 1;

    // Lưu ý: Bỏ comment đoạn code dưới đây nếu bạn đã tạo Entity Product 
    // để thiết lập mối quan hệ 1 Danh mục có nhiều Sản phẩm (1-N)
    /*
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
    */
}