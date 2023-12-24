package com.socialnetwork.socialnetwork.domain.validators;
import com.socialnetwork.socialnetwork.domain.Utilizator;

import com.socialnetwork.socialnetwork.domain.validators.ValidationException ;
import com.socialnetwork.socialnetwork.domain.validators.Validator ;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException{
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
    }


    /**
     * It must not be null
     * the first name must be less than 100 characters
     * it mustn't be empty
     * it's first character must be a letter
     */
    private void validateFirstName(String firstName) throws ValidationException{
        if(firstName == null)
            throw new ValidationException("First name must not be null!");
        else if (firstName.length() >=100)
            throw new ValidationException("First name is too long!");
        else if (firstName.isEmpty())
            throw new ValidationException("First name must not be empty!");
        else if (!Character.isAlphabetic(firstName.charAt(0)))
            throw new ValidationException("First name must start with a letter!");

    }


    /**
     * It must not be null
     * the first name must be less than 100 characters
     * it mustn't be empty
     * it's first character must be a letter
     */
    private void validateLastName(String lastName) throws ValidationException{
        if(lastName == null)
            throw new ValidationException("Last name must not be null!");
        else if (lastName.length() >=100)
            throw new ValidationException("Last name is too long!");
        else if (lastName.isEmpty())
            throw new ValidationException("Last name must not be empty!");
        else if (!Character.isAlphabetic(lastName.charAt(0)))
            throw new ValidationException("Last name must start with a letter!");
    }

}

