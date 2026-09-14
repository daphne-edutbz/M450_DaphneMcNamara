# Welche Elemente braucht es für eine Teststrategie? - Übungen

## Übung 1

**Wir haben folgende Beschreibung einer Verkaufssoftware:**


*Über die Verkaufssoftware kann das Autohaus seinen Verkäufern Rabattregeln vorgeben: Bei einem Kaufpreis von weniger
als 15’000 CHF soll kein Rabatt gewährt werden. Bei einem Preis bis zu 20’000 CHF sind 5% Rabatt angemessen. Liegt der
Kaufpreis unter 25’000 CHF sind 7% Rabatt möglich, darüber sind 8,5 % Rabatt zu gewähren.
Aufgabe
Leiten Sie aus dieser Beschreibung Testfälle ab. Wir wollen beide Varianten von Testfällen untersuchen.*


### Eine Tabelle mit abstrakten Testfällen. Hier verwenden Sie logische Operatoren wie > , < , etc.

| ID | Bedingung | Rabatt |
|---|---|---| 
| 1 | < 15'000 CHF | kein Rabatt, 0% |
| 2 |  15'000CHF <= Preis <= 20'000CHF   | 5% |
| 3 |  20'000CHF < Preis < 25'000        | 7% |
| 4 |  >= 25'000        | 8.5% |

 


### Eine Tabelle mit konkreten Testfällen. Hier verwenden Sie ganz konkrete Eingabe-Werte, um die Testfälle zu erstellen.

| ID | Kaufpreis | Rabatt |
|---|---|---| 
| 1 |  12'250CHF               | kein Rabatt, 0% |
| 2 |  18'000CHF | 5% |
| 3 |   24'000CHF        | 7% |
| 4 |  50'000       | 8.5% |


## Übung 2
**Suchen Sie sich eine Webseite zum Thema Autovermietung.
Definieren Sie funktionale Black-Box Tests, die Sie brauchen, um diese Plattform zu betreiben.
Listen Sie die 5 wichtigsten Testfälle auf
Erstellen Sie eine Tabelle mit diesen Testfälle als Markdown und stellen Sie diese in Ihr Repository.**

### Tabelle der Testfälle

**[Hertz](https://www.hertz.ch/rentacar/reservation/)**

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|---|---|---|---|---|---|
| 1 | Fahrzeugsuche mit gültiger Ort und Datum | Liste von verfügbaren Fahrzeuge wird angezeigt | Liste von verfügbaren Fahrzeugen wird angezeigt | Erfolgreich | - |
| 2 | Fahrzeugsuche mit Rückgabedatum VOR Abholdatum | Fehlermeldung, Buchung wird verhindert | Funktioniert nicht, wenn ich probiere das anzuwählen, springt es automatisch zum Abholdatum | Fehler | Keine Validierung des Rückgabedatums, System korrigiert Eingabe automatisch statt Fehler zu zeigen |
| 3 | Preisspanne ändern | Nur Autos in der Price Range werden angezeigt | Stimmt | Erfolgreich | - |
| 4 | Ungültige Kartennummer eingeben | Lässt Zahlung nicht durch | Rote Warnung "Ihre Kartennummer ist ungültig." | Erfolgreich | - |
| 5 | Ungültige Zahlungsdetails (Kartennummer, Ablaufdatum, Sicherheitscode) | Fehlermeldung bei allen 3 Feldern | Nur bei Kartennummer wird eine Fehlermeldung angezeigt | Fehler | Validierung für Ablaufdatum und Sicherheitscode fehlt oder ist unvollständig implementiert |




## Übung 3

**Projekt:** https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/tree/main/Unterlagen/teststrategie (bank-software-mvn)

### Black-Box Testfälle

| ID | Beschreibung | Erwartetes Resultat | Echtes Resultat | Status | Mögliche Ursache |
|---|---|---|---|---|---|
| 1 | Alle Konten anzeigen ("a") | Liste aller vorhandenen Konten mit Nr., Nachname, Währung | Liste korrekt angezeigt | Erfolgreich | - |
| 2 | Wechselkurs  mit gültigen Währungen (z. B. "CHF USD") abfragen | Umrechnungskurs angezeigt | Korrekter Kurs angezeigt | Erfolgreich | - |
| 3 | Wechselkurs abfragen mit "ungültigen" Währungen (z. B. "yen lira") | Fehlermeldung, erneute Eingabe möglich | Fehlermeldung erscheint, Eingabeschleife bleibt bestehen | Erfolgreich | - |
| 4 | Konto erstellen mit gültiger Währung (z. B. EUR) | Konto wird mit angegebener Währung erstellt | Konto wird korrekt erstellt | Erfolgreich | - |
| 5 | Konto erstellen mit ungültiger Währung (z. B. YEN) | Fehlermeldung, Konto wird nicht erstellt oder Nutzer wird zur Korrektur aufgefordert | Konto wird trotzdem erstellt, Währung wird stillschweigend auf USD gesetzt | Fehler | Fehlende strikte Validierung in `Counter.createAccount()` – ungültige Eingabe wird toleriert statt abgelehnt |
| 6 | Konto erstellen mit Nachname mit Sonderzeichen (z. B. "Nachname2$") | Fehlermeldung ODER Name wird abgelehnt | Name wird ohne jede Prüfung übernommen | Fehler | Keine Validierung des Namensfeldes in `Counter.createAccount()` |
| 7 | Einzahlen und Abheben auf eigenem Konto | Kontostand wird korrekt angepasst | Kontostand korrekt angepasst | Erfolgreich | - |
| 8 | Überweisung zwischen zwei Konten mit gleicher Währung (z. B. CHF → CHF) | Betrag wird 1:1 überwiesen | Funktioniert korrekt | Erfolgreich | - |
| 9 | Überweisung zwischen zwei Konten mit unterschiedlicher Währung (z. B. EUR → USD) | Betrag wird gemäss Wechselkurs umgerechnet | Keine Umrechnung, Betrag wird 1:1 übernommen ("Es wurde keine Umrechnung vorgenommen.") | Fehler | `convertCurrency()` in `Counter.java` deckt nicht alle Währungspaare ab (EUR↔CHF und EUR↔USD fehlen komplett) |
| 10 | Konto löschen mit Bestätigung "j" | Konto wird gelöscht | Konto wird gelöscht | Erfolgreich | - |
| 11 | Konto löschen mit Bestätigung "yes"/"no"/"ja" | Nur eindeutige Bestätigung sollte akzeptiert werden | "yes"/"no" werden abgelehnt (Abbruch), "ja" wird akzeptiert (Löschung) | Teilweise erfolgreich | `getConfirmation()` prüft nur den ersten Buchstaben – jedes Wort, das mit "j" beginnt (z. B. "jein"), würde ebenfalls als Bestätigung gewertet |
| 12 | Programm beenden ("q") | Programm beendet sich sauber | Programm beendet sich mit "Auf Wiedersehen!" | Erfolgreich |  - |
| 13 | Negativen Betrag einzahlen (z. B. -3242) | Fehlermeldung, Einzahlung wird verweigert | Betrag wird anstandslos abgezogen, Kontostand wird negativ (-1742.00 USD) | Fehler | `Account.deposit()` prüft nicht, ob amount negativ ist |
| 14 | Negativen Betrag abheben (z. B. -1341914) | Fehlermeldung, Abhebung wird verweigert | Betrag wird stattdessen dem Konto gutgeschrieben | Fehler | `withdraw()` prüft nur `amount > balance`; bei negativem amount ist diese Bedingung nie erfüllt, wodurch `balance -= amount` den Kontostand erhöht statt verringert |
| 15 | Betrag mit führenden Nullen eingeben (z. B. "000000") | Wird als 0 interpretiert, Kontostand bleibt gleich | Kontostand bleibt unverändert (0 wird korrekt addiert) | Erfolgreich | - |
| 16 | Ungültige Zeichen im Betrag (z. B. "4r3098984", "e", "0O934") | Fehlermeldung, erneute Eingabe möglich | Fehlermeldung erscheint korrekt, Eingabe wird wiederholt | Erfolgreich | - |
| 17 | Bei "Gewünschte Aktion" nur Leerzeichen eingeben | Fehlermeldung, erneute Eingabe möglich | Fehlermeldung erscheint korrekt | Erfolgreich | Kein Absturz, da Leerzeichen als Zeichen zählt und `substring(0,1)` nicht fehlschlägt |
| 18 | Ungültige Buchstabenkombination bei "Gewünschte Aktion" (z. B. "plkg") | Fehlermeldung, erneute Eingabe möglich | Fehlermeldung erscheint korrekt | Erfolgreich | - |
| 19 | Bei "Gewünschte Aktion" komplett leere Eingabe (direkt Enter drücken) | Fehlermeldung, erneute Eingabe möglich | Programm stürzt vollständig ab mit `StringIndexOutOfBoundsException: Range [0, 1) out of bounds for length 0` in `Counter.editAccount()` Zeile 105 | Fehler | `input.substring(0,1)` wird aufgerufen ohne vorher zu prüfen, ob `input` überhaupt ein Zeichen enthält |

### White-Box Testkandidaten

| Methode | Klasse | Was sollte getestet werden |
|---|---|---|
| `withdraw(double amount)` | Account | Grenzfälle: amount = balance, amount > balance, amount < 0, amount = 0 |
| `deposit(double amount)` | Account | Verhalten bei negativen Beträgen (keine Prüfung vorhanden) |
| `getAccount(int nr)` | Bank | Rückgabewert bei existierender/nicht existierender Kontonummer, negative Nummer |
| `convertCurrency(...)` | Counter | Alle 6 möglichen Währungspaare durchtesten (aktuell nur 3 implementiert) |
| `transferAmount(...)` | Counter | Verhalten bei zu hohem Betrag, bei unterschiedlichen Währungen, bei Betrag = 0 |
| `createAccount()` | Counter | Verhalten bei ungültiger Währung, bei leerem Namen, bei Namen mit Sonderzeichen |
| `getConfirmation()` | Counter | Verhalten bei leerer Eingabe, bei Wörtern die nicht exakt "j"/"n" sind |
| `chooseAccount()` | Counter | Regex-Verhalten bei gemischten Eingaben (z. B. "3x", "xyz1") |

### Verbesserungsvorschläge / Best Practices

- **Negative Beträge nicht geprüft (bestätigter kritischer Bug):**
`deposit()` und `withdraw()` prüfen nicht, ob übergebene Betrag negativ ist, 
getestet: negativer Einzahlungsbetrag zieht Geld vom Konto ab (Kontostand wurde negativ), ein negativer Abhebungsbetrag schreibt dem Konto Geld gut. Prüfung `if (amount <= 0) throw ...` in beiden Methoden wäre sinvoll und nötig


  
- **Unvollständige Währungsumrechnung:**
`convertCurrency()` in `Counter.java` deckt nur USD↔CHF und USD→EUR ab, nicht aber EUR↔CHF oder EUR→USD, Bei diesen Kombinationen wird Betrag unverändert übernommen, faktisch "erschafft" oder "vernichtet" das Geld beim Transfer,
Sinnvoller: bereits vorhandene `ExchangeRateOkhttp`-Klasse zu nutzen statt hartcodierter Kurse



- **Stillschweigendes Fallback bei ungültiger Währung:** Bei Kontoerstellung wird unbekannte Währung nur mit Warnung akzeptiert und automatisch auf USD gesetzt, statt Eingabe abzulehnen, kann zu unbeabsichtigten Konten führen


- **Keine Validierung des Namensfeldes:** Sonderzeichen, Zahlen oder leere Strings werden beim Nachnamen anstandslos akzeptiert.


- **Mögliche Exception bei leerer Eingabe:**  In `editAccount()` (Zeile 105) und `getConfirmation()` wird `input.substring(0,1)` aufgerufen, ohne vorher zu prüfen, ob String überhaupt Zeichen enthält, bei leerer Eingabe (nur Enter drücken) stürzt Applikation mit unbehandelten StringIndexOutOfBoundsException komplett ab (<img width="858" height="293" alt="image" src="https://github.com/user-attachments/assets/cdf9f835-5554-47a4-85b6-a3ccf9bf1f61" />
  ),
  Auffällig: Im Hauptmenü (chooseAccount()) wird dieselbe Art von leerer Eingabe korrekt mit einer Fehlermeldung abgefangen –> Inputvalidierung ist inkonsistent im Code umgesetzt


- **Bestätigungslogik zu ungenau:** `getConfirmation()` akzeptiert jedes Wort, das mit "j" beginnt, als Bestätigung,  exakter Vergleich mit `equalsIgnoreCase("j")` oder `equalsIgnoreCase("ja")` wäre sicherer

 
- **Hartcodierter API-Key:** In `ExchangeRateOkhttp.java` ist API-Key direkt im Quellcode sichtbar, aus Sicherheitsgründen sollte dieser über Umgebungsvariable oder Konfigurationsdatei eingebunden werden, statt fest im Code zu stehen

 
- **Keine Wiederverwendung gelöschter Kontonummern:** Nach Löschen eines Kontos wird dessen Nummer nicht neu vergeben, da  `counter` in `Account` nie zurückgesetzt wird. Das ist an sich kein Fehler, sollte aber dokumentiert sein, damit es kein Verwirrung stiftet


