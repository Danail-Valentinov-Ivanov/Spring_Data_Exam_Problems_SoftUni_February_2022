package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportSellerDTO;
import softuni.exam.models.dto.ImportSellerRootDTO;
import softuni.exam.models.entity.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerServiceImpl implements SellerService {

    private final String READ_SELLER_PATH = "src/main/resources/files/xml/sellers.xml";
    private final SellerRepository sellerRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    @Autowired
    public SellerServiceImpl(SellerRepository sellerRepository, ValidationUtil validationUtil
            , ModelMapper modelMapper) throws JAXBException {
        this.sellerRepository = sellerRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportSellerRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return sellerRepository.count() > 0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(Path.of(READ_SELLER_PATH));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        ImportSellerRootDTO importSellerRootDTO = (ImportSellerRootDTO) unmarshaller
                .unmarshal(new File(READ_SELLER_PATH));
        return importSellerRootDTO.getImportSellerDTOList().stream()
                .map(importSellerDTO -> importSeller(importSellerDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importSeller(ImportSellerDTO importSellerDTO) {
        if (!validationUtil.isValid(importSellerDTO)){
            return "Invalid seller";
        }
//        if (!validationUtil.isValidEnum(importSellerDTO.getRating())){
//            return "Invalid seller";
//        }
        Optional<Seller> optionalSeller = sellerRepository.findByEmail(importSellerDTO.getEmail());
        if (optionalSeller.isPresent()){
            return "Invalid seller";
        }
        Seller seller = modelMapper.map(importSellerDTO, Seller.class);
        sellerRepository.save(seller);
        return "Successfully import seller " + seller;
    }
}
