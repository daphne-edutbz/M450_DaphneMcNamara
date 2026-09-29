# Automation Testing - Aufgaben

## Übung 1 - REST-Schnittstelle automatisiert testen

**Gewähltes Tool:** Postman (zum Tests definieren) + Newman (zur automatisierten CLI-Ausführung)

**Vorgehen:**
- Zwei Requests erstellt: `GET /students` und `POST /students`
- Pro Request Tests im "Tests"-Tab (Status Code, Antwortformat, Inhalt)
- Collection (als JSON) exportiert

### GET /students

<img width="929" height="568" alt="GETPostmanTests" src="https://github.com/user-attachments/assets/82258218-79a9-4cba-b52b-646f7473e96a" />

Für diesen Request drei Tests im "Tests"-Tab definiert:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});
pm.test("Contains at least one student", function () {
    pm.expect(pm.response.json().length).to.be.above(0);
});
```

**Was die Tests jeweils prüfen:**
- **Status code is 200:** Stellt sicher, dass Request überhaupt erfolgreich beantwortet wurde und keine Fehlermeldung (404/500 oder so) zurückkommt
- **Response is an array:** Prüft, dass Antwort im erwarteten Format zurückkommt, also als JSON-Array und nicht zb. als einzelnes Objekt oder leerer String
- **Contains at least one student:** Prüft inhaltlich, dass Liste nicht leer ist, sondern tatsächlich mindestens einen Studenten enthält -> bestätigt, dass die im Backend vordefinierten Studenten (Jonas, Patrick, Yves, Peter, Ann) korrekt aus DB geladen werden

### POST /students

<img width="950" height="569" alt="POSTPostmanTest" src="https://github.com/user-attachments/assets/6bc1df1a-c293-40c0-b20b-b2daaea159b8" />

Body als raw JSON:

```json
{
    "name": "Test Student",
    "email": "test@tbz.ch"
}
```

Test im "Tests"-Tab:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

**Was der Test prüft:**
- **Status code is 200:** Bestätigt, dass Backend neuen Studenten entgegengenommen und erfolgreich gespeichert hat, ohne Validierungsfehler oder Serverfehler

### Automatisierte Ausführung mit Newman

Nach Export der Collection als JSON über Kommandozeile mit Newman ausgeführt:

```
newman run "Student API Tests.postman_collection.json"
```

<img width="762" height="250" alt="RunNewman" src="https://github.com/user-attachments/assets/26c86f59-a98f-4e55-85fb-0701de367931" />

**Ergebnis:** Alle 4 Assertions über beide Endpoints erfolgreich (0 fehlgeschlagen)

<img width="604" height="482" alt="ResultsNewmanRun" src="https://github.com/user-attachments/assets/c9564aae-e091-40fe-806b-cee546e70c77" />


## Übung 2 - Frontend E2E-Test mit Cypress

**Gewähltes Tool:** Cypress

Cypress, weil es Browser-Interaktionen (Klicks, Formulareingaben, Navigation) tatsächlich in echten Browser automatisiert ausführt und dabei visuell mitverfolgt werden kann, was gerade passiert, ideal, um im Stile eines End-to-End-Tests die komplette Angular-Applikation aus Nutzersicht zu testen

### Installation und Setup

```
npm install cypress --save-dev
npx cypress open
```

Im Setup-Assistenten "E2E Testing" ausgewählt, danach Browser (ich hab Chrome) für automatisierte Ausführung

### Testcode

Datei: `cypress/e2e/student-flow.cy.js`

```javascript
describe('Student Management Flow', () => {

  it('should navigate to the student list and show students', () => {
    cy.visit('http://localhost:4200');
    cy.contains('List Students').click();
    cy.get('table').should('exist');
    cy.get('tbody tr').should('have.length.greaterThan', 0);
  });

  it('should navigate to the add-student form and create a new student', () => {
    cy.visit('http://localhost:4200');
    cy.contains('Add Students').click();

    cy.get('input[name="name"]').type('Cypress Test');
    cy.get('input[name="email"]').type('cypress@tbz.ch');
    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/students');
    cy.get('tbody').should('contain', 'Cypress Test');
  });

});
```

**Getestete Abläufe:**
1. Navigation zur Studentenliste über Button "List Students" und Prüfung, dass Tabelle existiert und mindestens einen Eintrag enthält
2. Navigation zum Formular über "Add Students", automatisches Ausfüllen von Name und E-Mail, Absenden des Formulars, und anschliessende Prüfung, dass App korrekt zur Liste zurücknavigiert und neu erstellte Student "Cypress Test" darin erscheint

**Ausführung:** `npx cypress open`, Tests laufen dabei automatisiert in einem echten, sichtbaren Browserfenster ab, Cypress steuert Klicks und Texteingaben komplett selbstständig, ohne manuelles Zutun.

Video der Testausführung:
https://github.com/user-attachments/assets/de736a45-fdb0-4a28-8b52-ba3a1cee524f

<img width="959" height="599" alt="CypressAutomatedTestPasses" src="https://github.com/user-attachments/assets/63c13aa1-995c-44c6-b159-559198d5caa0" />

**Ergebnis:** Beide Testfälle erfolgreich durchgelaufen

## Übung 3 - Last-Test mit JMeter

**Gewähltes Tool:** Apache JMeter

**Testaufbau:**
- Thread Group: 50 Nutzer, Ramp-up 5 Sekunden, 10 Loops pro Nutzer (= 500 Requests total)

<img width="754" height="468" alt="JMeterConfig" src="https://github.com/user-attachments/assets/208e86a0-8d64-4c36-875a-b1e27d1cad19" />

- HTTP Request: `GET http://localhost:8081/students`

<img width="958" height="565" alt="JMeterRun" src="https://github.com/user-attachments/assets/36857a1a-6ed4-4531-8eb4-a1af43f74f1b" />

**Was ich dafür hinzugefügt habe:**
- Rechtsklick auf Thread Group → Add → Sampler → HTTP Request, dort Server (`localhost`), Port (`8081`), Method (`GET`) und Path (`/students`) eingetragen
- Rechtsklick auf Thread Group → Add → Listener → Summary Report, um aggregierten Resultate (Durchschnitt, Fehlerrate, Durchsatz) nach Testlauf einsehen zu können

**Ergebnisse (Summary Report):**
- Durchschnittliche Antwortzeit: 90 ms -> bei "Average" im Summary Report
- Fehlerrate: 77.85 % -> "Error %"
- Durchsatz: 245.1 Requests/Sekunde -> "Throughput"

<img width="758" height="470" alt="JMeterSummaryReport2" src="https://github.com/user-attachments/assets/50c294e3-0dee-4309-8908-3bf35d5e11ea" />

- Erster Versuch:

<img width="959" height="566" alt="JMeterSummaryReport" src="https://github.com/user-attachments/assets/223f6f67-fad0-4932-8987-826864c27b6f" />

**Erkundete Funktionalitäten:**
- Skalierung der Nutzerzahl (50 → 500) und Auswirkung auf Antwortzeit
- View Results Tree zur Detailanalyse einzelner Requests

<img width="758" height="470" alt="JMeterSummaryReport2" src="https://github.com/user-attachments/assets/b5f38c86-db87-4844-b11a-8b7454bff857" />

- Test des POST-Endpoints mit Body-Daten

<img width="957" height="561" alt="JMeter500Threads" src="https://github.com/user-attachments/assets/88fb68c6-7c4c-4e5e-ba3b-fb9627caf760" />

**Erkenntnisse bei Erhöhung von 50 auf 500 gleichzeitige Nutzer:**
- durchschnittliche Antwortzeit nicht extrem gestiegen (von ca. 90 ms auf 82 ms)
- Fehlerrate durchgehend sehr hoch geblieben (77.85 % bzw. 71.13 %)
    -> Das zeigt, dass das Backend mit dem eingebetteten Tomcat-Server unter so hoher gleichzeitiger Last nicht mehr alle Requests annehmen kann, viele Verbindungen wurden vermutlich abgelehnt oder sind in einen Timeout gelaufen, bevor überhaupt eine Antwort zustande kam. Das
    -> macht deutlich, wieso Lasttests wichtig sind: Im normalen Betrieb mit wenigen Nutzern würde man gar nicht merken, dass die Applikation unter höherer Last so instabil wird. Für eine produktive Umgebung müsste man hier entweder die Thread-Pool-Konfiguration des Servers anpassen oder die Anwendung horizontal skalieren.

## Bonusaufgabe - Feature-Schätzung

### Feature-Definition
**Was:** Input-Validierung im Backend für die Student-Entity
**Ziel:** Verhindern, dass Student mit leerem Namen oder ungültiger E-Mail-Adresse gespeichert werden kann

**Geplante Umsetzung:**
- Jakarta Bean Validation Annotationen in `Student.java` hinzufügen: `@NotBlank` für Namen, `@Email` und `@NotBlank` für E-Mail
- `@Valid` Annotation beim `@RequestBody` im `StudentController` ergänzen, damit Validierung beim POST-Request tatsächlich greift
- Bei Validierungsfehler soll Backend automatisch mit Status 400 (Bad Request) antworten
- Betroffene Dateien: `Student.java`, `StudentController.java`, eventuell `pom.xml` (falls `spring-boot-starter-validation` noch fehlt)

**Geschätzter Zeitaufwand:** 10 Minuten

### Umsetzung

Zuerst Dependency für Bean Validation in der `pom.xml` ergänzt werden, da diese noch nicht vorhanden war:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Danach `Student`-Entity um Validierungs-Annotationen ergänzt:

```java
package ch.tbz.m450.testing.tools.repository.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@AllArgsConstructor
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    @NotBlank(message = "Email darf nicht leer sein")
    @Email(message = "Email muss gültig sein")
    private String email;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Student() {
        this.name = "";
        this.email = "";
    }
}
```

- `@NotBlank` verhindert leere oder nur aus Leerzeichen bestehende Werte für Name und Email
- `@Email` prüft zusätzlich, dass Email einem gültigen Email-Format entspricht

Zuletzt musste im `StudentController` die `@Valid`-Annotation beim `@RequestBody` ergänzt werden, da ohne diese Validierungs-Annotationen auf der Entity komplett ignoriert werden:

```java
package ch.tbz.m450.testing.tools.controller;

import ch.tbz.m450.testing.tools.repository.StudentRepository;
import ch.tbz.m450.testing.tools.repository.entities.Student;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/students")
    public List<Student> getStudents() {
        return (List<Student>) studentRepository.findAll();
    }

    @PostMapping("/students")
    void addStudent(@Valid @RequestBody Student user) {
        studentRepository.save(user);
    }
}
```

### Tests in Postman

**Leerer Name → 400 Bad Request:**

<img width="667" height="443" alt="BadRequestPostman" src="https://github.com/user-attachments/assets/1e6ecdb5-a343-4d14-a301-0483e7df4e9d" />

**Ungültige Email → 400 Bad Request:**

<img width="690" height="472" alt="EmailValidationPostman" src="https://github.com/user-attachments/assets/a16b7548-146a-49de-8b01-2b3de65a97af" />

**Gültige Daten → 200 OK (Kontrolltest, damit sicher ist, dass die Validierung nur bei wirklich ungültigen Daten greift):**

<img width="677" height="487" alt="ValidRequestPostman" src="https://github.com/user-attachments/assets/e661d48c-1604-4e16-9841-5d45119e942a" />

### Tatsächlicher Zeitaufwand
**Geschätzt:** 10 Minuten
**Tatsächlich:** ca. 15 Minuten

### Reflexion
Eigentliche Umsetzung (Dependency hinzufügen, Annotationen ergänzen, @Valid im Controller) hat ungefähr der Schätzung entsprochen, aber die zusätzliche Zeit kam nur daher, dass ich vergessen hatte, das Backend neu zu starten - Spring Boot übernimmt Änderungen nicht automatisch. Deshalb dachte ich kurz, die Validierung würde nicht funktionieren, dabei war einfach nur ein Neustart nötig. Lerneffekt: zuerst neu starten, bevor man den Code in Frage stellt.
