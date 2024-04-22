package com.example.quickcash;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quickcash.util.CredentialValidator;


public class CredentialValidatorJUnitTests {
    CredentialValidator validator;

    @Before
    public void setup() {
        validator = new CredentialValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }


    @Test
    public void checkIfEmailIsValid() {
        assertTrue(validator.isValidEmailAddress("matthew12394@gmail.com"));
        assertTrue(validator.isValidEmailAddress("ahmed.hamed@dal.ca"));
        assertTrue(validator.isValidEmailAddress("omar_alsadi123@dal.ca"));
        assertTrue(validator.isValidEmailAddress("akil@dal.ca"));
        assertTrue(validator.isValidEmailAddress("OmArAlSadi123@socialFrontier.com"));
    }


    @Test
    public void checkIfEmailIsInvalid() {
        assertFalse(validator.isValidEmailAddress("@dal.ca"));
        assertFalse(validator.isValidEmailAddress("isaacdal.ca"));
        assertFalse(validator.isValidEmailAddress("jamie@.ca"));
        assertFalse(validator.isValidEmailAddress("nethara@dal"));
        assertFalse(validator.isValidEmailAddress("!akil@dal"));
        assertFalse(validator.isValidEmailAddress("omar!@dal"));
    }

    @Test
    public void checkIfEmailIsEmpty() {
        assertTrue(validator.isEmpty(""));
    }

    @Test
    public void checkIfPasswordIsEmpty(){
        assertTrue(validator.isEmpty(""));
    }

    @Test
    public void checkIfPasswordIsValid(){
        assertTrue(validator.isValidPassword("abcd1234"));
    }

    @Test
    public void checkIfPasswordIsInValid(){
        assertFalse(validator.isValidPassword("acbd4"));
    }
    @Test
    public void checkIfRoleIsValid() {
        assertTrue(validator.isValidRole("Employee"));
        assertTrue(validator.isValidRole("Employer"));
    }

    @Test
    public void checkIfRoleIsInvalid() {
        assertFalse(validator.isValidRole("Select your role"));
    }
}
