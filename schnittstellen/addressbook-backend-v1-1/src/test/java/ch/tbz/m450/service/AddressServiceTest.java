package ch.tbz.m450.service;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address1;
    private Address address2;

    @BeforeEach
    void setUp() {
        address1 = new Address(1, "Daphne", "Sibel", "0791111111", new Date());
        address2 = new Address(2, "Lisa", "Ganz", "0792222222", new Date());
    }

    @Test
    void testSave() {
        when(addressRepository.save(address1)).thenReturn(address1);

        Address result = addressService.save(address1);

        assertEquals(address1, result);
        verify(addressRepository, times(1)).save(address1);
    }

    @Test
    void testGetAll_isSortedByComparator() {
        // Absichtlich in "falscher" Reihenfolge zurückgeben
        when(addressRepository.findAll()).thenReturn(List.of(address1, address2));

        List<Address> result = addressService.getAll();

        // Nach dem Comparator sollte "Ganz" alphabetisch zuerst kommen
        assertEquals("Ganz", result.get(0).getLastname());
        assertEquals("Sibel", result.get(1).getLastname());
    }

    @Test
    void testGetAddress_found() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(address1));

        Optional<Address> result = addressService.getAddress(1);

        assertTrue(result.isPresent());
        assertEquals("Daphne", result.get().getFirstname());
    }

    @Test
    void testGetAddress_notFound() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.getAddress(99);

        assertFalse(result.isPresent());
    }
}