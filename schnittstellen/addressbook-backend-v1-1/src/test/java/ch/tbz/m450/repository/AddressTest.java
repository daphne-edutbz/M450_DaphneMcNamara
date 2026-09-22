package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(1, "Daphne", "Sibel", "0791234567", new Date());
    }

    @Test
    void testAddressCreation() {
        assertEquals(1, address.getId());
        assertEquals("Daphne", address.getFirstname());
        assertEquals("Sibel", address.getLastname());
        assertEquals("0791234567", address.getPhonenumber());
        assertNotNull(address.getRegistrationDate());
    }

    @Test
    void testSetters() {
        address.setFirstname("Lisa");
        assertEquals("Lisa", address.getFirstname());
    }
}