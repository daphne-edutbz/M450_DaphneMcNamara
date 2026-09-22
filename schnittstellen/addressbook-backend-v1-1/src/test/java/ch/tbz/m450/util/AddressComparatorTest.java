package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressComparatorTest {

    private Address a1;
    private Address a2;

    @BeforeEach
    void setUp() {
        a1 = new Address(1, "Daphne", "Sibel", "0791111111", new Date());
        a2 = new Address(2, "Lisa", "Ganz", "0792222222", new Date());
    }

    @Test
    void testCompareSortsByLastname() {
        List<Address> list = new ArrayList<>(List.of(a1, a2));
        list.sort(new AddressComparator());

        // alphabetisch
        assertEquals("Ganz", list.get(0).getLastname());
        assertEquals("Sibel", list.get(1).getLastname());
    }

    @Test
    void testCompareEqualLastnames() {
        Address a3 = new Address(3, "Tim", "Ganz", "0793333333", new Date());
        // gleicher Nachname "Ganz", aber Vorname entscheidet
        int result = new AddressComparator().compare(a2, a3); // Lisa vs Tim
        assertTrue(result < 0); // "Lisa" kommt alphabetisch vor "Tim"
    }

    @Test
    void testCompareSameLastname_sortsByFirstname() {
        Address a1 = new Address(1, "Daphne", "Sibel", "0791111111", new Date());
        Address a2 = new Address(2, "Lisa", "Sibel", "0792222222", new Date());


        List<Address> list = new ArrayList<>(List.of(a1, a2));
        list.sort(new AddressComparator());

        assertEquals("Lisa", list.get(1).getFirstname());
        assertEquals("Daphne", list.get(0).getFirstname());
    }

    @Test
    void testCompareSameNames_sortsByRegistrationDate() throws InterruptedException {
        Date earlier = new Date();
        Thread.sleep(10);
        Date later = new Date();

        Address a1 = new Address(1, "Daphne", "Sibel", "0791111111", later);
        Address a2 = new Address(2, "Daphne", "Sibel", "0792222222", earlier);

        List<Address> list = new ArrayList<>(List.of(a1, a2));
        list.sort(new AddressComparator());

        assertEquals(earlier, list.get(0).getRegistrationDate());
    }

}