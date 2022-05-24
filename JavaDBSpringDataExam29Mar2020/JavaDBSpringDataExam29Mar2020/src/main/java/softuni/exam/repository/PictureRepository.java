package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Picture;

import java.util.Optional;
import java.util.Set;

//ToDo
@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Optional<Picture> findByName(String name);


    Optional<Set<Picture>> findAllByCarId(long id);
}
