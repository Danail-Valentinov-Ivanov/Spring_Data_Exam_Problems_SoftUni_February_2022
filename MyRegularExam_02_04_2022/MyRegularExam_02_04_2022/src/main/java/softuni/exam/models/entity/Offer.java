package softuni.exam.models.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //    accepts a positive number
    @Column(nullable = false)
    private BigDecimal price;

    //    "dd/MM/yyyy" format
    @Column(name = "published_on", nullable = false)
    private LocalDate publishedOn;

    //    Constraint: The offers table has a relation with the apartments table.
    @ManyToOne(optional = false)
    private Apartment apartment;

    //    Constraint: The offers table has a relation with the agents table.
    @ManyToOne(optional = false)
    private Agent agent;

    public Offer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDate publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return String.format("Agent %s %s with offer №%d:\n" +
                "\t-Apartment area: %.2f\n" +
                "\t--Town: %s\n" +
                "\t---Price: %.2f$", agent.getFirstName(), agent.getLastName(), id
                , apartment.getArea(), apartment.getTown().getTownName(), price);
    }
}
