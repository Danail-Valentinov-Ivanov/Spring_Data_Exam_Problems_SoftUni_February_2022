package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "apartment")
public class IdApartmentDto {

    @XmlElement
    private long id;

    public long getId() {
        return id;
    }
}
