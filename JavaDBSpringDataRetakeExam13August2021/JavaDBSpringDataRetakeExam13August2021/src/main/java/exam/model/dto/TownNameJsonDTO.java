package exam.model.dto;

import javax.validation.constraints.Size;

public class TownNameJsonDTO {

    @Size(min = 2)
    private String name;

    public TownNameJsonDTO() {
    }

    public String getName() {
        return name;
    }
}
