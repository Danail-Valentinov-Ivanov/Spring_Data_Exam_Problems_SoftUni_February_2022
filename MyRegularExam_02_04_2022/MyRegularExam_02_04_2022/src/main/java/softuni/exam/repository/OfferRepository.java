package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Offer;

import java.util.List;

// TODO:
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("select o from Offer o where o.apartment.apartmentType = :type" +
            " order by o.apartment.area desc, o.price asc ")
    List<Offer> findByApartmentApartmentTypeInOrderByApartmentAreaDescPriceAsc(ApartmentType type);
}
