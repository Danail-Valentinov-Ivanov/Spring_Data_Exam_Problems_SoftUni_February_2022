package exam.model.dto;

import javax.validation.constraints.Size;

public class ShopNameJsonDto {

    @Size(min = 4)
    private String name;

    public ShopNameJsonDto() {
    }

    public String getName() {
        return name;
    }
}
