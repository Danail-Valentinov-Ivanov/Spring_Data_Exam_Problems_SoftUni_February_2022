package softuni.exam.models.entity;

import softuni.exam.models.entity.enums.DaysOfWeek;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "forecasts")
public class Forecast extends BaseEntity{

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DaysOfWeek daysOfWeek;

//between -20 and 60 (both numbers are INCLUSIVE
    @Column(name = "max_temperature", nullable = false)
    private double maxTemperature;

    //between -50 and 40 (both numbers are INCLUSIVE
    @Column(name = "min_temperature", nullable = false)
    private double minTemperature;

    @Column(nullable = false)
    private LocalTime sunrise;

    @Column(nullable = false)
    private LocalTime sunset;

    @ManyToOne
    private City city;

//    Constraint: The forecasts table has а relation with the cities table.

    public Forecast() {
    }

    public DaysOfWeek getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public LocalTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return String.format("City: %s:\n\t-min temperature: %.2f\n\t--max temperature: %.2f\n" +
                "\t---sunrise: %s\n\t----sunset: %s", city.getCityName(), minTemperature, maxTemperature
        ,sunrise, sunset);
    }
}
