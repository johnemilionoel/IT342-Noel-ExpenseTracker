package edu.cit.noel.expensetracker.validation;

import edu.cit.noel.expensetracker.exception.ValidationException;

public interface ValidationStrategy<T> {
    void validate(T request) throws ValidationException;
}
