package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.ImportLaptopDTO;
import exam.model.entity.Laptop;
import exam.model.entity.Shop;
import exam.model.entity.WarrantyType;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LaptopServiceImpl implements LaptopService {

    private final String READ_LAPTOP_PATH = "src/main/resources/files/json/laptops.json";
    private final LaptopRepository laptopRepository;
    private final ShopRepository shopRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public LaptopServiceImpl(LaptopRepository laptopRepository, ShopRepository shopRepository
            , Validator validator, ModelMapper modelMapper, Gson gson) {
        this.laptopRepository = laptopRepository;
        this.shopRepository = shopRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        Path path = Path.of(READ_LAPTOP_PATH);
        return Files.readString(path);
    }

    @Override
    public String importLaptops() throws IOException {
        ImportLaptopDTO[] importLaptopDTOS = gson.fromJson(readLaptopsFileContent(), ImportLaptopDTO[].class);
        return Arrays.stream(importLaptopDTOS)
                .map(importLaptopDTO -> importLaptop(importLaptopDTO))
                .collect((Collectors.joining("\n")));
    }

    private String importLaptop(ImportLaptopDTO importLaptopDTO) {
        Set<ConstraintViolation<ImportLaptopDTO>> validate = validator.validate(importLaptopDTO);
        if (!validate.isEmpty()){
            return "Invalid Laptop";
        }
        Optional<Laptop> optionalLaptop = laptopRepository.findByMacAddress(importLaptopDTO.getMacAddress());
        if (optionalLaptop.isPresent()){
            return "Invalid Laptop";
        }
        if (!isValid(importLaptopDTO.getWarrantyType())){
            return "Invalid Laptop";
        }
        Optional<Shop> optionalShop = shopRepository.findByName(importLaptopDTO.getShop().getName());
        Laptop laptop = modelMapper.map(importLaptopDTO, Laptop.class);
        laptop.setShop(optionalShop.get());
        laptopRepository.save(laptop);
        return String.format("Successfully imported Laptop %s", laptop);
    }

    private boolean isValid(WarrantyType warrantyType) {
        return WarrantyType.BASIC.equals(warrantyType) || WarrantyType.PREMIUM.equals(warrantyType)
                || WarrantyType.LIFETIME.equals(warrantyType);
    }

    @Override
    public String exportBestLaptops() {
        List<Laptop> laptops = laptopRepository
                .findByOrderByCpuSpeedDescRamDescStorageDescMacAddressAsc();
        return laptops.stream().map(laptop -> laptop.exportLaptopData())
                .collect(Collectors.joining("\n"));
    }
}
