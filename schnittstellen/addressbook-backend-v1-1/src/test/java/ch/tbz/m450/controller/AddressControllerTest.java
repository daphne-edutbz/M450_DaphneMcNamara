package ch.tbz.m450.controller;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(1, "Daphne", "Sibel", "0791234567", new Date());
    }

    @Test
    void testCreateAddress() {
        when(addressService.save(address)).thenReturn(address);

        ResponseEntity<Address> response = addressController.createAddress(address);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(address, response.getBody());
    }

    @Test
    void testGetAddresses() {
        when(addressService.getAll()).thenReturn(List.of(address));

        ResponseEntity<List<Address>> response = addressController.getAddresses();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAddress_found() {
        when(addressService.getAddress(1)).thenReturn(Optional.of(address));

        ResponseEntity<Address> response = addressController.getAddress(1);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetAddress_notFound() {
        when(addressService.getAddress(99)).thenReturn(Optional.empty());

        ResponseEntity<Address> response = addressController.getAddress(99);

        assertEquals(404, response.getStatusCode().value());
    }
}