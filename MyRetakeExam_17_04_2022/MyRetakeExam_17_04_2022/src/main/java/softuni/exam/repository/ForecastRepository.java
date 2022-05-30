package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Forecast;
import softuni.exam.models.entity.enums.DaysOfWeek;

import java.util.List;
import java.util.Optional;

// TODO:
@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    Optional<Forecast> findByDaysOfWeekAndCityId(DaysOfWeek daysOfWeek, Long city);


    List<Forecast> findAllByDaysOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc
            (DaysOfWeek sunday, int i);
}
