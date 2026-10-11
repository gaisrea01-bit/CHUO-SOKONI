package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    List<Category> findByParentCategoryCategoryId(Integer categoryId);

    List<Category> findByParentCategoryIsNull();

    List<Category> findByStatus(String status);

    boolean existsByName(String name);
}