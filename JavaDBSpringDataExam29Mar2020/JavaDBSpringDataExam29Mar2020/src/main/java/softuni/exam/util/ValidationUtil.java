package softuni.exam.util;

import softuni.exam.models.entity.enums.Rating;

import javax.validation.ConstraintViolation;


// ToDo Implement interface 
public interface ValidationUtil {

    <E> boolean isValid(E entity);

//    boolean isValidEnum(Rating rating);
}
