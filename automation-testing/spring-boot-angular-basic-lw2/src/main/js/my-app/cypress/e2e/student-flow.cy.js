describe("Student Management Flow", () => {
  it("should navigate to the student list and show students", () => {
    cy.visit("http://localhost:4200");
    cy.contains("List Students").click();
    cy.get("table").should("exist");
    cy.get("tbody tr").should("have.length.greaterThan", 0);
  });

  it("should navigate to the add-student form and create a new student", () => {
    cy.visit("http://localhost:4200");
    cy.contains("Add Students").click();

    cy.get('input[name="name"]').type("Cypress Test");
    cy.get('input[name="email"]').type("cypress@tbz.ch");
    cy.get('button[type="submit"]').click();

    // Nach dem Submit navigiert deine App laut student-form.component.ts
    // automatisch zurück zu /students
    cy.url().should("include", "/students");
    cy.get("tbody").should("contain", "Cypress Test");
  });
});
