package softuni.exam.models.entity;

import org.hibernate.annotations.NotFound;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "cars")
public class Car extends BasicEntity{

//    between 2 to 20 exclusive
    @Column(nullable = false)
    private String make;

//    between 2 to 20 exclusive
    @Column(nullable = false)
    private String model;

    private int kilometers;

    @Column(name = "registered_on")
    private LocalDate registeredOn;

    @OneToMany(mappedBy = "car", targetEntity = Picture.class)
    private Set<Picture> pictureSet;

//    The combination of make, model and kilometers makes a car unique.

    public Car() {
    }

    public Set<Picture> getPictureSet() {
        return pictureSet;
    }

    public void setPictureSet(Set<Picture> pictureSet) {
        this.pictureSet = pictureSet;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String toExportMethod(){
        return String.format("Car make - %s, model - %s\n\tKilometers - %d\n\tRegisteredOn - %s\n" +
                        "\tNumber of pictures - %d\n", make, model, kilometers
                , registeredOn, pictureSet.size());
    }
}
