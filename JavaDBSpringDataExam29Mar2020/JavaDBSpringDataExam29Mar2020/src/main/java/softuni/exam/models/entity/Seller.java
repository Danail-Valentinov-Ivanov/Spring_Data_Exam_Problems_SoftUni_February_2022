package softuni.exam.models.entity;

import softuni.exam.models.entity.enums.Rating;

import javax.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends BasicEntity{

//    between 2 to 20 exclusive
    @Column(name = "first_name")
    private String firstName;

//    between 2 to 20 exclusive
    @Column(name = "last_name")
    private String lastName;

//    must contains ‘@’ and ‘.’ – dot
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(nullable = false)
    private String town;

    public Seller() {
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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", lastName, email);
    }
}
