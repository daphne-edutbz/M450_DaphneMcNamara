# Schnittstellen - Übungen

## Aufgabe 1

### Comparator korrigieren
- ursprüngliche Implementierung fehlerhaft, gab immer `-1` zurück, egal was verglichen wurde:

```java
@Override
public int compare(Address a1, Address a2) {
    // Wrong implementation, please change me
    return -1;
}
```

- Korrigiert, sodass nach Nachname sortiert wird:

```java
package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;

import java.util.Comparator;

public class AddressComparator implements Comparator<Address> {

    @Override
    public int compare(Address a1, Address a2) {
        return a1.getLastname().compareTo(a2.getLastname());
    }

}
```

### AddressTest - Entity

```java
package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(1, "Max", "Muster", "0791234567", new Date());
    }

    @Test
    void testAddressCreation() {
        assertEquals(1, address.getId());
        assertEquals("Max", address.getFirstname());
        assertEquals("Muster", address.getLastname());
        assertEquals("0791234567", address.getPhonenumber());
        assertNotNull(address.getRegistrationDate());
    }

    @Test
    void testSetters() {
        address.setFirstname("Anna");
        assertEquals("Anna", address.getFirstname());
    }
}
```


<img width="931" height="554" alt="AddressTestsPass" src="https://github.com/user-attachments/assets/b2feb61d-713a-4224-9436-bb792c23e418" />


### AddressComparatorTest - sortiert Comparator korrekt?

```java
package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        // alphabetisch: "Ganz" kommt vor "Sibel"
        assertEquals("Ganz", list.get(0).getLastname());
        assertEquals("Sibel", list.get(1).getLastname());
    }

    @Test
    void testCompareEqualLastnames() {
        Address a3 = new Address(3, "Tim", "Ganz", "0793333333", new Date());
        int result = new AddressComparator().compare(a2, a3);
        assertEquals(0, result); // gleicher Nachname -> 0
    }
}
```

**Wichtig:** 
`a2` und `a3` müssen für diesen Test denselben Nachnamen haben ("Ganz"), sonst liefert `compareTo` keine 0, sondern die Buchstabendifferenz


<img width="932" height="558" alt="ComparatorTestsPass" src="https://github.com/user-attachments/assets/90a778ab-24bb-45f7-8fb8-c31e3b7fc7be" />


### AddressServiceTest - H2-Datenbank wegmocken

- zentrale Teil der Aufgabe:
   `AddressService` hängt vom `AddressRepository` ab, welches intern mit der H2-Datenbank spricht, mit Mockito wird dieses Repository durch ein Test Double ersetzt, damit der Test nicht wirklich auf die Datenbank zugreift

```java
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
```

<img width="927" height="520" alt="ServiceTestsPass" src="https://github.com/user-attachments/assets/9a51d016-1ce6-4bff-925b-1ae94f1c6abf" />


**Erklärung zu den Test Doubles:**
- `@Mock` erstellt Fake-Repository, das nie wirklich mit der Datenbank spricht
- `@InjectMocks` injiziert Fake-Repository automatisch in Service
- `when(...).thenReturn(...)` **Stub-Verhalten**, geben vor, was zurückkommt
- `verify(...)` **Mock-Verhalten** - prüfen, ob Methode wirklich aufgerufen wurde

### AddressControllerTest - Controller isoliert testen

- Hier wird Service gemockt (NICHT Repository), um Controller unabhängig zu testen:

```java
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
        address = new Address(1, "Max", "Muster", "0791234567", new Date());
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
```

<img width="929" height="561" alt="controllerTestsPass" src="https://github.com/user-attachments/assets/46708fb3-1510-4702-a86d-e8372fc683b7" />



### Testergebnisse
Alle Tests über `AddressTest`, `AddressComparatorTest`, `AddressServiceTest` und `AddressControllerTest` laufen erfolgreich durch.


<img width="959" height="565" alt="A1-AllTestsPass" src="https://github.com/user-attachments/assets/09878725-b143-4bf0-b622-fa681a86c550" />


## Aufgabe 2

### Comparator erweitern
- Comparator erweitert, sodass bei gleichem Nachnamen zusätzlich nach Vorname und bei gleichem Vornamen zusätzlich nach Registrierungsdatum sortiert wird:

```java
package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;

import java.util.Comparator;

public class AddressComparator implements Comparator<Address> {

    @Override
    public int compare(Address a1, Address a2) {
        int result = a1.getLastname().compareTo(a2.getLastname());
        if (result == 0) {
            result = a1.getFirstname().compareTo(a2.getFirstname());
        }
        if (result == 0) {
            result = a1.getRegistrationDate().compareTo(a2.getRegistrationDate());
        }
        return result;
    }
}
```

### Tests für die neue Funktionalität

```java
@Test
void testCompareSameLastname_sortsByFirstname() {
    Address a1 = new Address(1, "Daphne", "Sibel", "0791111111", new Date());
    Address a2 = new Address(2, "Lisa", "Sibel", "0792222222", new Date());

    List<Address> list = new ArrayList<>(List.of(a1, a2));
    list.sort(new AddressComparator());

    assertEquals("Daphne", list.get(0).getFirstname());
    assertEquals("Lisa", list.get(1).getFirstname());
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
```

<img width="921" height="571" alt="A2-NewComparatorTestsPass" src="https://github.com/user-attachments/assets/18a08841-25d6-4648-94c0-4358dd014fe4" />


### Bug im ursprünglichen testCompareEqualLastnames

- Nach Erweiterung des Comparators aus Aufgabe 2 ist mir aufgefallen, dass der bestehende Test `testCompareEqualLastnames` aus Aufgabe 1 nicht mehr richtig funktioniert:

```java
@Test
void testCompareEqualLastnames() {
    Address a3 = new Address(3, "Tim", "Ganz", "0793333333", new Date());
    int result = new AddressComparator().compare(a2, a3);
    assertEquals(0, result); // gleicher Nachname -> 0
}
```

Fehlermeldung: `Expected :0, Actual :-8`

**Ursache:** `a2` (Nachname "Ganz", Vorname "Lisa") und `a3` (Nachname "Ganz", Vorname "Tim") haben zwar denselben Nachnamen, aber unterschiedliche Vornamen, 
weil erweiterte Comparator bei gleichem Nachnamen zusätzlich den Vornamen vergleicht, kommt hier nicht mehr 0 heraus, sondern die Differenz zwischen "Lisa" und "Tim", 
ursprüngliche Test war nur solange korrekt, wie Comparator ausschliesslich den Nachnamen berücksichtigt hat

**Korrektur:**

```java
    @Test
    void testCompareEqualLastnames() {
        Address a3 = new Address(3, "Tim", "Ganz", "0793333333", new Date());
        // gleicher Nachname "Ganz", aber Vorname entscheidet
        int result = new AddressComparator().compare(a2, a3); // Lisa vs Tim
        assertTrue(result < 0); // "Lisa" kommt alphabetisch vor "Tim"
    }
```

### Testergebnisse
Alle Tests, inklusive der neuen und korrigierten Testfälle, laufen erfolgreich durch.

<img width="928" height="576" alt="A2-AllTestsPass" src="https://github.com/user-attachments/assets/20781315-367c-4c08-8e59-af71a1e109f1" />

