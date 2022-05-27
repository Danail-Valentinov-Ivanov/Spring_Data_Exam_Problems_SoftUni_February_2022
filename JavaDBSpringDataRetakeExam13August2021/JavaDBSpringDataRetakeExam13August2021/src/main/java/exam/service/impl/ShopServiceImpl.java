package exam.service.impl;

import exam.model.dto.ImportShopDTO;
import exam.model.dto.ImportShopRootDTO;
import exam.model.entity.Shop;
import exam.model.entity.Town;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.validation.Validator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final String READ_SHOP_PATH = "src/main/resources/files/xml/shops.xml";
    private final ShopRepository shopRepository;
    private final TownRepository townRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    public ShopServiceImpl(ShopRepository shopRepository, TownRepository townRepository
            , Validator validator, ModelMapper modelMapper) throws JAXBException {
        this.shopRepository = shopRepository;
        this.townRepository = townRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        JAXBContext jaxbContext = JAXBContext.newInstance(ImportShopRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return shopRepository.count() > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        Path path = Path.of(READ_SHOP_PATH);
        return Files.readString(path);
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {
        ImportShopRootDTO importShopRootDTO = (ImportShopRootDTO) unmarshaller
                .unmarshal(new FileReader(READ_SHOP_PATH));
        return importShopRootDTO.getImportShopDTOList().stream()
                .map(importShopDTO -> importShop(importShopDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importShop(ImportShopDTO importShopDTO) {
        Set<ConstraintViolation<ImportShopDTO>> validate = validator.validate(importShopDTO);
        if (!validate.isEmpty()){
            return "Invalid shop";
        }
        Optional<Shop> optionalShop = shopRepository.findByName(importShopDTO.getName());
        if (optionalShop.isPresent()){
            return "Invalid shop";
        }
        Shop shop = modelMapper.map(importShopDTO, Shop.class);
        Optional<Town> optionalTown = townRepository.findByName(importShopDTO.getTown().getName());
        shop.setTown(optionalTown.get());
        shopRepository.save(shop);
        return String.format("Successfully imported Shop %s - %f", shop.getName(), shop.getIncome());
    }
}
