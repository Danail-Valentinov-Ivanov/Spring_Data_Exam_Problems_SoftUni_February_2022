package softuni.exam.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cities")
public class City extends BaseEntity{

//    between 2 to 60 inclusive
    @Column(name = "city_name", nullable = false, unique = true)
    private String cityName;

//    min 2 symbols
    @Column(columnDefinition = "TEXT")
    private String description;

//more than or equal to 500.   Cannot be null.
    private int population;

    @ManyToOne
    private Country country;

//    Constraint: The cities table has а relation with the countries table.

    public City() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
