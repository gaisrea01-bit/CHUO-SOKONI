package tz.ac.mzumbe.chuosokoni.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.ac.mzumbe.chuosokoni.model.University;

@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {

    Optional<University> findByName(String name);

    List<University> findByLocation(String location);

    boolean existsByName(String name);

    List<University> findByStatus(String status);
}