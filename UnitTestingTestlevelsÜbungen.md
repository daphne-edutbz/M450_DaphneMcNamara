# Unit Testing & Testlevels - Übungen

## Aufgabe 1 - Simpler Rechner

### 1. Einbindung JUnit 5 Dependency (`pom.xml`)
Damit JUnit 5 im Projekt nutzbar ist, `junit-jupiter`-Dependency in `pom.xml`:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

### 2. Code


- `Calculator.java` - Klasse mit grundlegenden mathematischen Operationen:


``` java
package ch.tbz.calculator;

public class Calculator {
    public double add(double a, double b) {
        return a + b; 
    }
    
    public double subtract(double a, double b) { return a - b; }
    public double multiply(double a, double b) { return a * b; }
    public double divide(double a, double b) { return a / b; }
}
```

- `CalculatorTest.java` - Unit-Tests aller Operationen und Sonderfalls, Division durch 0 (Ctrl+Shift+T macht Klasse automatisch):

``` Java
package ch.tbz.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    Calculator calc = new Calculator();

    @Test
    void add() {
        assertEquals(5.0, calc.add(2, 3));
    }

    @Test
    void subtract() {
        assertEquals(5.0, calc.subtract(10, 5));
    }

    @Test
    void multiply() {
        assertEquals(5.0, calc.multiply(1, 5));
    }

    @Test
    void divide() {
        assertEquals(5.0, calc.divide(10, 2));
    }

    @Test
    void testDivideByZero() {
        assertEquals(Double.POSITIVE_INFINITY, calc.divide(1, 0));
    }
}
```

### 3. Testergebnisse & Ausführung

#### A) Ausführung in Entwicklungsumgebung (IntelliJ IDEA)
Alle 5 Tests laufen in IntelliJ erfolgreich durch (5/5 passed):


<img width="932" height="569" alt="5TestsPass" src="https://github.com/user-attachments/assets/5801c3d5-1712-450f-aa26-1e09fdac4ba2" />


#### B) Ausführung auf Kommandozeile (mvn test)
Über Terminal im Projektordner liefert mvn test erfolgreiches Ergebnis (BUILD SUCCESS):


<img width="946" height="559" alt="KommandozeilePass" src="https://github.com/user-attachments/assets/36fd99ec-ad33-4fcb-ba4d-35698af4023b" />


#### C) Ausführung über Maven-Tool-Fenster in IntelliJ
Ausführung des Maven-Lifecycles Lifecycle -> test verläuft erfolgreich:


<img width="959" height="554" alt="MavenPass" src="https://github.com/user-attachments/assets/03d43a47-c752-4b40-a86b-f2e588e7e503" />



## Aufgabe 2 - JUnit Zusammenfassung

Wichtigsten Annotations, Assertions und Features von JUnit 5 beim Schreiben von Unit-Tests:

### 1. Grundlegende Annotations

* **`@Test`**: Markiert Methode als Testmethode, die vom Test-Runner ausgeführt wird

  ```java
  @Test
  void testAddition() {
      Calculator calc = new Calculator();
      assertEquals(5.0, calc.add(2, 3));
  }
  ```

* **`@BeforeEach / @AfterEach`**: Läuft jeweils vor bzw. nach JEDER EINZELNEN Testmethode in der Klasse (ideal für Initialisieren/Aufraumen von Testdaten/Objekten) 

``` java
private Calculator calc;

@BeforeEach
void setUp() {
    calc = new Calculator(); // Wird vor jedem Test frisch instanziiert
}

@AfterEach
void tearDown() {
    calc = null;
}
```

* **`@BeforeAll / @AfterAll`**: Läuft EXAKT EINMAL vor bzw. nach ALLEN Tests der gesamten Testklasse (z. B. für teure Setup-Prozesse wie Datenbankverbindungen), Methoden müssen `static` sein  
``` java
@BeforeAll
static void initAll() {
    System.out.println("Starte die Testsuite...");
}

@AfterAll
static void tearDownAll() {
    System.out.println("Testsuite beendet.");
}
```

* **`@DisplayName`**: Ermöglicht benutzerdefinierten, gut lesbaren Namen für Testmethode im Test-Explorer
``` java
@Test
@DisplayName("Sollte zwei positive Zahlen korrekt addieren")
void testAddPositiveNumbers() {
    assertEquals(10, calc.add(4, 6));
}
```



* **`@Disabled`**: Deaktiviert Testmethode temporär, sodass sie beim Ausführen übersprungen wird
``` java
@Disabled("Wird erst nach Behebung des Bugs #42 wieder aktiviert")
@Test
void testFeatureInDevelopment() {
    // Testcode...
}
```

### 2. Assertions (Überprüfungen)
Assertions prüfen, ob tatsächliche Ergebnis (actual) dem erwarteten Ergebnis (expected) entspricht:

``` java
import static org.junit.jupiter.api.Assertions.*;

@Test
void testAssertions() {
    // assertEquals: Prüft Gleichheit zweier Werte
    assertEquals(4.0, calc.multiply(2, 2));

    // assertTrue / assertFalse: Prüft Wahrheitswerte
    assertTrue(calc.add(2, 2) == 4);
    assertFalse(calc.add(2, 2) == 5);

    // assertThrows: Prüft, ob eine erwartete Exception geworfen wird
    assertThrows(ArithmeticException.class, () -> {
        int result = 5 / 0;
    });

    // assertAll: Gruppiert mehrere Assertions (alle werden ausgeführt, selbst wenn eine fehlschlägt)
    assertAll("Calculator Grundoperationen",
        () -> assertEquals(5.0, calc.add(2, 3)),
        () -> assertEquals(1.0, calc.subtract(3, 2)),
        () -> assertEquals(6.0, calc.multiply(2, 3))
    );
}
```


### 3. Parameterisierte Tests (`@ParameterizedTest`)
Ermöglicht es, denselben Test mehrmals mit unterschiedlichen Eingabewerten durchzulaufen:

``` java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedTest
@ValueSource(doubles = {1.0, 2.5, 10.0, 100.0})
void testAddWithPositiveNumbers(double number) {
    assertTrue(calc.add(number, 1.0) > number);
}
```

### 4. Referenzen
- Official User Guide: JUnit 5 User Guide [https://docs.junit.org/6.1.3/overview.html]



## Aufgabe 3 - Banken Simulation

<img width="934" height="513" alt="bank6_klassendiagramm" src="https://github.com/user-attachments/assets/7c94c9f3-7618-4ce3-a093-f81ee89077f6" />


### 1. Klassen und ihre Aufgaben
- **`Bank`**: Hauptklasse, Verwaltung des Gesamtsystems -> erstellt neue Konten, verwaltet Kontenliste, führt Transaktionen (Ein-/Auszahlungen) über Kontonummern aus, generiert Auswertungen (z. B. Gesamtsaldo, Top/Bottom-Listen)
- **`Account` (abstrakte Klasse)**: Basisklasse für alle Kontoarten -> kapselt grundlegenden Eigenschaften wie Kontonummer (`id`), aktuellen Kontostand (`balance`), Historieneinträge aller getätigten Buchungen (`bookings`)
- **`SavingsAccount`**: Erbt von `Account`, repräsentiert Standard-Sparkonto, erlaubt keine Überziehung (Kontostand darf nicht unter 0 fallen)
- **`SalaryAccount`**: Erbt von `Account`, Repräsentiert Lohnkonto mit festgelegten Kreditlimit (`creditLimit`), Kontostand darf bis zu diesem Limit ins Negative fallen
- **`PromoYouthSavingsAccount`**: Erbt von `SavingsAccount`, spezielles Jugendsparkonto, das bei Einzahlungen zusätzlich Promotion-Bonus auf eingezahlten Betrag gewährt
- **`Booking`**: Repräsentiert einzelne Buchung/Transaktion auf Konto mit einem Datum (`date`), einem Betrag (`amount`) und einer Print-Methode für den Kontoauszug
- **`AccountBalanceComparator`**: Implementiert das `Comparator`-Interface, zum Sortieren von Konten nach Kontostand (z. B. für Ermittlung der Top-5-Konten)


### 2. Zusammenhänge der Klassen
- **Vererbung (Inheritance)**: 
  * `SavingsAccount` und `SalaryAccount` erweitern abstrakte Basisklasse `Account`
  * `PromoYouthSavingsAccount` erweitert `SavingsAccount` (Spezialisierung eines Sparkontos)

- **Komposition / Aggregation**:
  * **`Bank` $\rightarrow$ `Account`**:  `Bank` verwaltet Sammlung (z. B. `TreeMap`) von `Account`-Objekten, indiziert nach Konto-ID
  * **`Account` $\rightarrow$ `Booking`**: Jedes `Account`-Objekt speichert Liste (`ArrayList<Booking>`) aller darauf durchgeführten Buchungen

- **Nutzungsbeziehung**:
  * `Bank` nutzt `AccountBalanceComparator`, um Kontenliste für Ausgabe der Top- und Bottom-Konten zu sortieren


### 3. Wichtige Methoden und ihre Funktion
- **`Account.deposit(date, amount)`**: Zahlt Betrag auf Konto ein, prüft zuvor mit `canTransact(date)`, ob Buchungsdatum nicht vor letzten Buchung liegt
- **`Account.withdraw(date, amount)`**: Hebt Betrag ab, spezifischen Unterklassen (`SavingsAccount`, `SalaryAccount`) überschreiben bzw. ergänzen Logik, um einzuhalten, ob Konto überzogen werden darf
- **`Account.canTransact(date)`**: Stellt sicher, dass Transaktionen nur in chronologischer Reihenfolge durchgeführt werden (Datum darf nicht älter als letzte Buchung sein)
- **`Account.print(year, month)`**: Druckt detaillierten Kontoauszug für bestimmten Monat aus
- **`Bank.createSavingsAccount()` / `createSalaryAccount()`**: Erstellt neues Konto der jeweiligen Kategorie, generiert eindeutige ID und fügt es der Bank hinzu
- **`Bank.printTop5()` / `printBottom5()`**: Sortiert alle Konten mithilfe des `AccountBalanceComparator` nach Saldo und gibt 5 Konten mit höchsten bzw. niedrigsten Salden aus


## Aufgabe 4 - Unit-Tests implementieren

### Umsetzung
- JUnit-5-Tests für alle Klassen des Bankensystems umgesetzt
- ursprünglichen `fail("toDo")`-Platzhalter durch Test-Abläufe ersetzt
- Erfolgsfälle (z. B. korrekte Einzahlungen/Abhebungen) & Fehlerfälle (z. B. negative Beträge oder Kreditlimit-Überschreitungen) abgedeckt

### Testergebnis
- Alle **20 Unit-Tests** wurden über Maven (`mvn clean test`) erfolgreich und ohne Fehler ausgeführt (**BUILD SUCCESS**)

<img width="959" height="574" alt="All20TestsPass" src="https://github.com/user-attachments/assets/ef153511-fbd6-49a7-988b-6f63b1b26999" />


### Code Coverage (Testabdeckung)

<img width="957" height="571" alt="RanWithCoverage" src="https://github.com/user-attachments/assets/79b866e5-dfbe-4c2f-91fa-92bca0463bbf" />


**Endergebnis:**

<img width="583" height="337" alt="coverage" src="https://github.com/user-attachments/assets/a37771cf-7cf2-4241-8868-87abdc4f996a" />




