# Automation Testing - Aufgaben

## Übung 1 - REST-Schnittstelle automatisiert testen

**Gewähltes Tool:** Postman (zum Tests definieren) + Newman (zur automatisierten CLI-Ausführung)

**Vorgehen:**
- Zwei Requests erstellt: `GET /students` und `POST /students`
- Pro Request Tests im "Tests"-Tab definiert (Status Code, Antwortformat, Inhalt)
- Collection als JSON exportiert

### GET /students

<img width="929" height="568" alt="GETPostmanTests" src="https://github.com/user-attachments/assets/82258218-79a9-4cba-b52b-646f7473e96a" />

Für diesen Request wurden drei Tests im "Tests"-Tab definiert:

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
- **Status code is 200:** Stellt sicher, dass Request erfolgreich beantwortet wurde und keine Fehlermeldung (z. B. 404 oder 500) zurückkommt
- **Response is an array:** Prüft, dass Antwort im erwarteten Format zurückkommt, also als JSON-Array und nicht zum Beispiel als einzelnes Objekt oder leerer String
- **Contains at least one student:** Prüft inhaltlich, dass Liste nicht leer ist, sondern tatsächlich mindestens einen Studenten enthält -> bestätigt, dass die im Backend vordefinierten Studenten (Jonas, Patrick, Yves, Peter, Ann) korrekt aus der Datenbank geladen werden

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
- **Status code is 200:** Bestätigt, dass Backend den neuen Studenten entgegengenommen und erfolgreich gespeichert hat, ohne Validierungsfehler oder Serverfehler

### Automatisierte Ausführung mit Newman

Nach dem Export der Collection als JSON wird sie über die Kommandozeile mit Newman ausgeführt:

```
newman run "Student API Tests.postman_collection.json"
```

<img width="762" height="250" alt="RunNewman" src="https://github.com/user-attachments/assets/26c86f59-a98f-4e55-85fb-0701de367931" />

**Ergebnis:** Alle 4 Assertions über beide Endpoints erfolgreich (0 fehlgeschlagen)

<img width="604" height="482" alt="ResultsNewmanRun" src="https://github.com/user-attachments/assets/c9564aae-e091-40fe-806b-cee546e70c77" />


## Übung 2 - Frontend E2E-Test mit Cypress

**Gewähltes Tool:** Cypress

Cypress, weil es Browser-Interaktionen (Klicks, Formulareingaben, Navigation) tatsächlich in einem echten Browser automatisiert ausführt und dabei visuell mitverfolgt werden kann, was gerade passiert, ideal, um im Stile eines End-to-End-Tests die komplette Angular-Applikation aus Nutzersicht zu testen.

### Installation und Setup

```
npm install cypress --save-dev
npx cypress open
```

Im Setup-Assistenten wurde "E2E Testing" ausgewählt, danach ein Browser (Chrome) für die automatisierte Ausführung.

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

**Ausführung:** `npx cypress open`, Tests laufen dabei automatisiert in echten, sichtbaren Browserfenster ab, Cypress steuert Klicks und Texteingaben komplett selbstständig, ohne manuelles Zutun

Video der Testausführung:
https://github.com/user-attachments/assets/de736a45-fdb0-4a28-8b52-ba3a1cee524f

<img width="959" height="599" alt="CypressAutomatedTestPasses" src="https://github.com/user-attachments/assets/63c13aa1-995c-44c6-b159-559198d5caa0" />

**Ergebnis:** Beide Testfälle erfolgreich durchgelaufen
