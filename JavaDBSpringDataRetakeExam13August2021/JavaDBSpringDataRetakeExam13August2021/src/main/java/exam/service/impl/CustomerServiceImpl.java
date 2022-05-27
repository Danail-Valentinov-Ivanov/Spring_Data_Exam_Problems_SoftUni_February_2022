package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.ImportCustomerDTO;
import exam.model.entity.Customer;
import exam.model.entity.Town;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final String READ_CUSTOMER_PATH = "src/main/resources/files/json/customers.json";
    private final CustomerRepository customerRepository;
    private final TownRepository townRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Gson gson;

    public CustomerServiceImpl(CustomerRepository customerRepository, TownRepository townRepository
            , Validator validator, ModelMapper modelMapper, Gson gson) {
        this.customerRepository = customerRepository;
        this.townRepository = townRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        this.gson = gson;

        modelMapper.addConverter(mapping -> LocalDate.parse(mapping.getSource()
                , DateTimeFormatter.ofPattern("dd/MM/yyyy")), String.class, LocalDate.class);
    }

    @Override
    public boolean areImported() {
        return customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        Path path = Path.of(READ_CUSTOMER_PATH);
        return Files.readString(path);
    }

    @Override
    public String importCustomers() throws IOException {
        ImportCustomerDTO[] importCustomerDTOS = gson
                .fromJson(readCustomersFileContent(), ImportCustomerDTO[].class);
        return Arrays.stream(importCustomerDTOS)
                .map(importCustomerDTO -> importCustomer(importCustomerDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importCustomer(ImportCustomerDTO importCustomerDTO) {
        Set<ConstraintViolation<ImportCustomerDTO>> validate = validator.validate(importCustomerDTO);
        if (!validate.isEmpty()){
            return "Invalid Customer";
        }
        Optional<Customer>optionalCustomer = customerRepository.findByEmail(importCustomerDTO.getEmail());
        if (optionalCustomer.isPresent()){
            return "Invalid Customer";
        }
        Customer customer = modelMapper.map(importCustomerDTO, Customer.class);
        Optional<Town>optionalTown = townRepository.findByName(importCustomerDTO.getTown().getName());
        customer.setTown(optionalTown.get());
        customerRepository.save(customer);
        return String.format("Successfully imported Customer %s", customer.toString());
    }
}
