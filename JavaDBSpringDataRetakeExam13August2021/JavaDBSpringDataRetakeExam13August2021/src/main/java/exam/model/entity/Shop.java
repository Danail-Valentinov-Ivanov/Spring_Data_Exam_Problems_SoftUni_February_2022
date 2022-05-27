package exam.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //    higher than or equal to 4
    @Column(nullable = false, unique = true)
    private String name;

//    more than or equal to 20000.
    @Column(nullable = false)
    private BigDecimal income;

    //    higher than or equal to 4
    @Column(nullable = false)
    private String address;

//    between 1 and 50
    @Column(name = "employee_count")
    private int employeeCount;

//    more than or equal to 150.
    private int shopArea;

//    Constraint: The shops table has a relation with the towns table.
    @ManyToOne
    private Town town;

    public Shop() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public int getShopArea() {
        return shopArea;
    }

    public void setShopArea(int shopArea) {
        this.shopArea = shopArea;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }
}
