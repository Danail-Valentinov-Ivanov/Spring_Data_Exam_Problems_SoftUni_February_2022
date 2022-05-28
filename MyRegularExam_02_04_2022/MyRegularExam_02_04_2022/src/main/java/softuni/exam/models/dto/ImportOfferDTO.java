package softuni.exam.models.dto;

import softuni.exam.models.entity.Agent;

import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "offer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportOfferDTO {

    @XmlElement
    @Positive
    private BigDecimal price;

    @XmlElement
    private NameAgentDTO agent;

    @XmlElement
    private IdApartmentDto apartment;

    @XmlElement
    private String publishedOn;

    public BigDecimal getPrice() {
        return price;
    }

    public NameAgentDTO getAgent() {
        return agent;
    }

    public IdApartmentDto getApartment() {
        return apartment;
    }

    public String getPublishedOn() {
        return publishedOn;
    }
}
