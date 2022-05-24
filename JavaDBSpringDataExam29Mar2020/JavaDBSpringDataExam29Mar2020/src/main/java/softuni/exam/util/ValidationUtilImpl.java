package softuni.exam.util;

import org.springframework.stereotype.Component;
import softuni.exam.models.entity.enums.Rating;

import javax.validation.Validation;
import javax.validation.Validator;

@Component
public class ValidationUtilImpl implements ValidationUtil {

    private final Validator validator;

    public ValidationUtilImpl() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public <E> boolean isValid(E entity) {
        return validator.validate(entity).isEmpty();
    }

//    @Override
//    public boolean isValidEnum(Rating rating) {
//        return Rating.GOOD.equals(rating) || Rating.BAD.equals(rating) || Rating.UNKNOWN.equals(rating);
//    }
}
