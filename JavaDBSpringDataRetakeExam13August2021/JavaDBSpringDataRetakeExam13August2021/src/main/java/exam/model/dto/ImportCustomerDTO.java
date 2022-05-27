package exam.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class ImportCustomerDTO {

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;

    @Email
    private String email;

    private String registeredOn;

    private TownNameJsonDTO town;

    public ImportCustomerDTO() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public TownNameJsonDTO getTown() {
        return town;
    }
}
