package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.iotstar.entity.Category;
// LƯU Ý: Nếu file Repository của bạn tên là CategoryRepository (không có chữ I), hãy sửa lại dòng import dưới đây cho khớp.
import vn.iotstar.repository.CategoryRepository; 
import vn.iotstar.services.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        // Đã sửa lại lỗi chữ C viết hoa thành chữ c viết thường
        this.categoryRepository = categoryRepository; 
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Page<Category> search(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            // Lưu ý: Đảm bảo ICategoryRepository của bạn có phương thức findByCategorynameContaining()
            return categoryRepository.findByCategorynameContaining(keyword, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    @Override
    public <S extends Category> S save(S entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }
}