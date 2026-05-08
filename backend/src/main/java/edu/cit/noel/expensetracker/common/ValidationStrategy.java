package edu.cit.noel.expensetracker.common;

import edu.cit.noel.expensetracker.common.ValidationException;

public interface ValidationStrategy<T> {
    void validate(T request) throws ValidationException;
}
