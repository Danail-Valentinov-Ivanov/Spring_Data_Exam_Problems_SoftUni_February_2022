package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Car;

import java.util.List;
import java.util.Optional;

//ToDo
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByMakeAndModelAndKilometers(String make, String model, int kilometers);

    @Query("select c from Car c order by size(c.pictureSet) desc, c.make asc")
    List<Car> findAllByOrderByPicturesCountDescMakeAsc();
}
