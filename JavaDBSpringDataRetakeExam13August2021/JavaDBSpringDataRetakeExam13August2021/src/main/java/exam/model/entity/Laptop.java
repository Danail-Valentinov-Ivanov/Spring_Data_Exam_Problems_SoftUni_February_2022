package exam.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "laptops")
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    higher than 8.
    @Column(name = "mac_address", nullable = false, unique = true)
    private String macAddress;

//    accepts positive floating-point numbers.
    @Column(name = "cpu_speed", nullable = false)
    private double cpuSpeed;

//    more than or equal to 8 and less than or equal to 128
    private int ram;

//    more than or equal to 128 and less than or equal to 1024
    private int storage;

//     higher than or equal to 10.
    @Column(nullable = false, columnDefinition = "text")
    private String description;

//    positive number.
    @Column(nullable = false)
    private BigDecimal price;

//    BASIC, PREMIUM, LIFETIME
    @Column(name = "warranty_type", nullable = false)
    private WarrantyType warrantyType;

//    Constraint: The laptops table has a relation with the shops table.
    @ManyToOne
    private Shop shop;

    public Laptop() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public WarrantyType getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(WarrantyType warrantyType) {
        this.warrantyType = warrantyType;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f - %d - %d", macAddress, cpuSpeed, ram, storage);
    }

    public String exportLaptopData() {
        return String.format("Laptop - %s\n*CpuSpeed - %.2f\n**Ram - %d\n***Storage - %d\n" +
                "****Price - %.2f\n#Shop name - %s\n##Town - %s\n", macAddress, cpuSpeed, ram, storage
        ,price, shop.getName(), shop.getTown().getName());
    }
}
