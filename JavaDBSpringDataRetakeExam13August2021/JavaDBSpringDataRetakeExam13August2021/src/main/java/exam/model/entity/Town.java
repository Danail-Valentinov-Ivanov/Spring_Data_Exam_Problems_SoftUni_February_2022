package exam.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "towns")
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    higher than or equal to 2
    @Column(nullable = false, unique = true)
    private String name;

//    must be positive
    private int population;

//    higher than or equal to 10
    @Column(name = "travel_guide", nullable = false, columnDefinition = "text")
    private String travelGuide;

    public Town() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public String getTravelGuide() {
        return travelGuide;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setTravelGuide(String travelGuide) {
        this.travelGuide = travelGuide;
    }
}
