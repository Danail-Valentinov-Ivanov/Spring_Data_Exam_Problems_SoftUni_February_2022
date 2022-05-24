package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.Size;

public class ImportPictureDTO {

    @Expose
    @Size(min = 2, max = 19)
    private String name;

    @Expose
    private String dateAndTime;

    @Expose
    private long car;

    public ImportPictureDTO() {
    }

    public String getName() {
        return name;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public long getCar() {
        return car;
    }
}
