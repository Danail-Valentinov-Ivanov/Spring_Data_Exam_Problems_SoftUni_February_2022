package exam.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "shops")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportShopRootDTO {

    @XmlElement(name = "shop")
    private List<ImportShopDTO>importShopDTOList;

    public List<ImportShopDTO> getImportShopDTOList() {
        return importShopDTOList;
    }
}
