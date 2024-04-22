package com.example.quickcash;

import com.example.quickcash.util.JobValidator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * This suite tests the JobValidator class
 */
public class JobValidatorUnitTests {

    JobValidator validator;
    @Before
    public void setup() {
        validator = new JobValidator();
    }

    @Test
    public void checkIfTitleIsValid() {
        assertTrue(validator.isValidTitle("Beta Tester"));
    }

    @Test
    public void checkIfTitleIsInvalid() {
        assertFalse(validator.isValidTitle("Tester"));
    }

    @Test
    public void checkIfDescriptionIsValid() {
        assertTrue(validator.isValidDescription("Test video games"));
    }

    @Test
    public void checkIfDescriptionIsInvalid() {
        assertFalse(validator.isValidDescription("Test game"));
    }

    @Test
    public void checkIfTypeIsValid(){
        assertTrue(validator.isValidJobType("Part time"));
        assertTrue(validator.isValidJobType("Full time"));
        assertTrue(validator.isValidJobType("Contract"));
    }

    @Test
    public void checkIfTypeIsInvalid(){
        assertFalse(validator.isValidJobType(""));
    }
}
