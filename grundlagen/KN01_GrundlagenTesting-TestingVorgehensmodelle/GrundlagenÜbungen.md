# Grundlagen zu Testing und Testing in Vorgehensmodelle - Aufgaben

## Aufgabe 1
**Welche Formen von Tests kennen Sie aus der Informatik? Wie werden die Tests durchgeführt?**
- End-to-End Test (E2E): Durchläuft kompletten Ablauf, von Benutzeroberfläche bis Datenbank aus Nutzersicht
- Integration Test: Prüft Zusammenspiel und Schnittstellen mehrerer Module oder Systeme
- Component Test: Testet zusammenhängende Komponent (z. B. ein Suchfeld) als Ganzes
- Unit Test: Prüft einzelne Funktion oder Methode isoliert auf ihre Logik

**Beispiel aus der Praxis: Programmieren eines Online-Shops**
1. Unit Test
- Testet wirklich NUR eine einzelne Funktion isoliert im Code
- **Zum Beispiel:** Funktion berechneRabatt(), die prüft, ob ein 10%-Gutschein den Preis eines Produkts im Warenkorb korrekt reduziert
- **Durchführung:** Automatisiert über Test-Frameworks (z. B. PyTest), Entwickler gibt feste Werte vor und prüft, ob die Funktion das exakte Ergebnis liefert

2. Integration Test
- Testet das Zusammenspiel mehrerer Funktionen, Module oder externer Systeme
- **Zum Beispiel:** Einbinden einer Zahlungsmethode, wenn das Shop-System eine Bezahlanfrage sendet und die Antwort des Dienstleisters verarbeitet
- **Durchführung:** Automatisiert über API-Testtools (z. B. Postman)

3. E2E Test
- Testet vollständigen Prozess über gesamte Anwendung aus Nutzersicht
- **Zum Beispiel:** komplette Bestellvorgang (Produkt in den Warenkorb legen, Adresse eingeben, bezahlen und Bestätigungsseite anzeigen)
- **Durchführung:** Automatisiert mit Browser-Automatisierungstools (z. B. Cypress), um Klick- und Eingabeverhalten eines echten Nutzers im Browser zu simulieren

## Aufgabe 2
**Nennen Sie ein Beispiel eines SW-Fehlers und eines SW-Mangels. Nennen Sie ein Beispiel für einen hohen Schaden bei einem SW-Fehler.**

**Fehler:**
- Anforderung nicht erfüllt, Abweichung zwischen IST-Verhalten (also was das System während des Tests macht) und SOLL-Verhalten (was in der Spezifikation oder in den Anforderungen festgelegt wurde)
- **Beispiel:** Online-Shop berechnet bei einem Gutscheincode von 10 % fälschlicherweise 50 % Rabatt, weil in einer Formel im Code ein falscher Rechenoperator verwendet wurde

**Mangel:**
- gestellte Anforderung oder berechtigte Erwartung nicht angemessen erfüllt z.b. eine Berechnung wird korrekt ausgeführt, aber nicht korrekt dargestellt
- **Beispiel:** Rabattberechnung stimmt zwar, aber Ergebnisfeld auf dem Smartphone-Bildschirm ist so abgeschnitten, dass der Kunde den Endpreis nicht vollständig lesen kann

**Beispiel für einen hohen Schaden durch einen SW-Fehler**

Cadbury Schokoladenberg (2006) – ERP-System-Fehler
- **Schaden:** Ca. 48 Millionen Euro Verlust durch unverkaufbare Überproduktion und logistische Probleme
- **Ursache:** Fehler bei Implementierung des neuen ERP-Systems (SAP) führte dazu, dass die Nachfrage falsch berechnet wurde, System löste eine massive Überproduktion von Schokoladenriegeln aus, die weder rechtzeitig ausgeliefert noch verkauft werden konnten

## Aufgabe 3

### Code

**Preisberechnung.java**

```java
public class Preisberechnung {
    double calculatePrice(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;
        double result;

        if (extras >= 3)
            addon_discount = 10;
        else if (extras >= 5) // Mistake
            addon_discount = 15;
        else
            addon_discount = 0;

        if (discount > addon_discount)
            addon_discount = discount;

        result = baseprice/100.0 * (100-discount) + specialprice
                + extraprice/100.0 * (100-addon_discount);

        return result;
    }
}
```

**PreisberechnungTest.java**

```java
public class PreisberechnungTest {

    static Preisberechnung pb = new Preisberechnung();

    public static void main(String[] args) {
        boolean result = test_calculate_price();
        System.out.println("Alle Tests bestanden: " + result);
    }

    static boolean test_calculate_price() {
        double price;
        boolean test_ok = true;

        // Testfall 1: nur Grundpreis, keine Rabatte
        price = pb.calculatePrice(1000, 0, 0, 0, 0);
        if (price != 1000) {test_ok = false;
            System.out.println("Testfall 1 NICHT bestanden: " + price);
        }

        // Test case 2: discount applies only to baseprice
        price = pb.calculatePrice(1000, 0, 0, 2, 10);
        if (price != 900) {test_ok = false;
        System.out.println("Testfall 2 NICHT bestanden: " + price);
        }

        // Test case 3: boundary value 2 extras -> no addon_discount yet
        price = pb.calculatePrice(0, 0, 100, 2, 0);
        if (price != 100) {test_ok = false;
        System.out.println("Testfall 3 NICHT bestanden: " + price);}

        // Test case 4: boundary value 3 extras -> 10% addon_discount
        price = pb.calculatePrice(0, 0, 100, 3, 10);
        if (price != 90) {test_ok = false;
        System.out.println("Testfall 4 NICHT bestanden: " + price);}

        // Test case 5: 4 extras -> still 10% addon_discount
        price = pb.calculatePrice(0, 0, 100, 4, 0);
        if (price != 90) {test_ok = false;
        System.out.println("Testfall 5 NICHT bestanden: " + price);
        }

        // Test case 6: boundary value 5 extras -> should be 15% addon_discount
        price = pb.calculatePrice(0, 0, 100, 5, 0);
        if (price != 85) {test_ok = false;
        System.out.println("Testfall 6 NICHT bestanden: " + price);}

        // Test case 7: specialprice is always added in full, without any discount
        price = pb.calculatePrice(0, 500, 0, 0, 50);
        if (price != 500) {test_ok = false;
        System.out.println("Testfall 7 NICHT bestanden: " + price);
        }

        // Test case 8: combination - all parameters active at the same time
        // baseprice (1000 - 20% = 800) + specialprice (200) + extraprice (100 - 20% [discount > addon_discount] = 80)
        price = pb.calculatePrice(1000, 200, 100, 3, 20);
        if (price != 1080) {test_ok = false;
        System.out.println("Testfall 8 NICHT bestanden: " + price);}

        return test_ok;
    }

}
```

### Tests runnen

<img width="927" height="495" alt="TestFails" src="https://github.com/user-attachments/assets/7d65ad9e-4a80-4237-a3de-f8e21e67642b" />

Fehler:

<img width="914" height="515" alt="MistakeCode" src="https://github.com/user-attachments/assets/d33cda56-40b2-450a-b55b-c075927d2933" />

### Erklärung

```java
if (extras >= 3)          // Schritt 1: Ist 5 >= 3? Ja -> true
    addon_discount = 10;  // Schritt 2: Rabatt zu 10 und if-statement verlassen
else if (extras >= 5)     // Schritt 3: nie ausgeführt
    addon_discount = 15;
```
- Weil 5 grösser als/gleich gross wie 3, ist erster if-statement true, Discount wird gegeben und beachtet else-if gar nicht
- -> alle nummern 5 oder grösser erfüllen erste if statment, also unmöglich 15% discount


## Aufgabe 3 - Bonus

### Fix

<img width="877" height="533" alt="NachFix" src="https://github.com/user-attachments/assets/52535692-24ec-445c-9103-83a8a8872bd5" />

``` java
public class Preisberechnung {
    double calculatePrice(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;
        double result;

        if (extras >= 5)
            addon_discount = 15;
        else if (extras >= 3)
            addon_discount = 10;
        else
            addon_discount = 0;

        if (discount > addon_discount)
            addon_discount = discount;

        result = baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);

        return result;
    }
}
```


Fix funktionert jetzt, weil man bei if-else immer die höchste nummer zuerst auswerten:

```java
if (extras >= 5)          // Schritt 1: Ist 5 >= 5? Ja -> true
    addon_discount = 15;  // Schritt 2: discount zu 15 und schleife verlassen
else if (extras >= 3)     // Nur duchgeführt, wenn extras 3/4 ist 
    addon_discount = 10;
else                      // Nur duchgeführt, wenn extras 0,1 oder 2 ist
    addon_discount = 0;
```
- extras = 5:  extras >= 5 -> discount auf 15%
- extras = 4: erfüllt nicht extras >= 5, also extras >= 3 -> discount auf 10%
- extras = 2: erfüllt beides nicht, geht zu else -> discount auf 0%

