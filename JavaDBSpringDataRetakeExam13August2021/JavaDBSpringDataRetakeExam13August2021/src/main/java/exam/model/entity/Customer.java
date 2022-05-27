package exam.model.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //    higher than or equal to 2.
    @Column(name = "first_name", nullable = false)
    private String firstName;

    //    higher than or equal to 2.
    @Column(name = "last_name", nullable = false)
    private String lastName;

    //    (must contains '@' and '.' – a dot). The values are unique in the database.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    //    Constraint: The customers table has а relation with the towns table.
    @ManyToOne
    private Town town;

    public Customer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    @Override
    public String toString() {
        return firstName + lastName + " - " + email;
    }
}
