package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class ImportCarDTO {

    @Expose
    @Size(min = 2, max = 19)
    private String make;

    @Expose
    @Size(min = 2, max = 19)
    private String model;

    @Expose
    @Positive
    private int kilometers;

    @Expose
    private String registeredOn;

    public ImportCarDTO() {
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getKilometers() {
        return kilometers;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }
}
