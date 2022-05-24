package softuni.exam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import softuni.exam.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class ApplicationBeanConfiguration {

	//ToDo

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }

//    @Bean
//    public ValidationUtil validationUtil() {
//        return null;
//    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(mapping -> LocalDate.parse(mapping.getSource()
                , DateTimeFormatter.ofPattern("dd/MM/yyyy")), String.class, LocalDate.class);

        modelMapper.addConverter(mapping -> LocalDateTime.parse(mapping.getSource()
                , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), String.class, LocalDateTime.class);
        return modelMapper;
    }

}
