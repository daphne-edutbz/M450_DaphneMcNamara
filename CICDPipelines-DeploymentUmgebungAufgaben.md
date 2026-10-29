# Automatisiertes Testen und Deployen & Deployment Environment - Übungen

## Aufgabe 1 - Unit Testing im Backend

### 1. Controller-Tests mit MockMvc (`RecipeControllerTest`)
**Ziel:** Simulation von HTTP-Requests, ohne dass echter Tomcat-Server hochgefahren werden muss,  alle zentralen Endpunkte des `RecipeController` getestet

**Testcode-Ausstattung:**
- `getRecipes_returns200AndList`: Prüft GET-Endpoint auf erfolgreiches Resultat (200 OK) & ob Liste korrekt zurückgegeben wird
- `getRecipe_existingId_returns200AndRecipe`: Prüft Abrufen eines einzelnen Rezepts anhand einer UUID
- `addRecipe_returns200AndRecipe`: Prüft POST-Endpoint zum Erstellen eines Rezepts inklusive JSON-Payload und Verifizierung des Services

**Code:**

```java
package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecipeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeService service;

    @Mock
    private RecipeEntityMapper mapper;

    @InjectMocks
    private RecipeController recipeController;

    private ObjectMapper objectMapper;
    private UUID recipeId;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
        objectMapper = new ObjectMapper();

        recipeId = UUID.randomUUID();
        Ingredient ingredient = new Ingredient(UUID.randomUUID(), "Tomato", "Big", Unit.PIECE, 5);
        recipe = new Recipe(recipeId, "Lasagne", "Lecker", "http://image.url", List.of(ingredient));
    }

    @Test
    void getRecipes_returns200AndList() throws Exception {
        when(service.getRecipes()).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Lasagne"));
    }

    @Test
    void getRecipe_existingId_returns200AndRecipe() throws Exception {
        when(service.getRecipeById(recipeId)).thenReturn(recipe);

        mockMvc.perform(get("/api/recipes/recipe/{id}", recipeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeId.toString()))
                .andExpect(jsonPath("$.name").value("Lasagne"));
    }

    @Test
    void addRecipe_returns200AndRecipe() throws Exception {
        when(service.addRecipe(any(Recipe.class))).thenReturn(recipe);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lasagne"));

        verify(service).addRecipe(any(Recipe.class));
    }
}
```

### 2. Mapper-Tests mit SoftAssertions (`RecipeEntityMapperTest` & `IngredientEntityMapperTest`)
**Ziel:** Validierung MapStruct-Mapper zwischen Entitäten (Datenbank) und Domänen-Modellen, dabei bewusst SoftAssertions eingesetzt


**`IngredientEntityMapperTest.java`**
``` java
package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class IngredientEntityMapperTest {

    private IngredientEntityMapper mapper;
    private UUID id;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IngredientEntityMapper.class);
        id = UUID.randomUUID();
    }

    @Test
    void entityToDomain_mapsAllFields() {
        IngredientEntity entity = new IngredientEntity(id, "Milk", "Fresh", Unit.DECILITRE, 3);

        Ingredient ingredient = mapper.entityToDomain(entity);

        assertSoftly(softly -> {
            softly.assertThat(ingredient.getId()).isEqualTo(id);
            softly.assertThat(ingredient.getName()).isEqualTo("Milk");
            softly.assertThat(ingredient.getComment()).isEqualTo("Fresh");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.DECILITRE);
            softly.assertThat(ingredient.getAmount()).isEqualTo(3);
        });
    }

    @Test
    void domainToEntity_mapsAllFields() {
        Ingredient ingredient = new Ingredient(id, "Milk", "Fresh", Unit.DECILITRE, 3);

        IngredientEntity entity = mapper.domainToEntity(ingredient);

        assertSoftly(softly -> {
            softly.assertThat(entity.getId()).isEqualTo(id);
            softly.assertThat(entity.getName()).isEqualTo("Milk");
            softly.assertThat(entity.getComment()).isEqualTo("Fresh");
            softly.assertThat(entity.getUnit()).isEqualTo(Unit.DECILITRE);
            softly.assertThat(entity.getAmount()).isEqualTo(3);
        });
    }
}
```


**`RecipeEntityMapperTest.java`**
``` java
package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RecipeEntityMapperTest {

    private RecipeEntityMapper mapper;
    private UUID recipeId;
    private UUID ingredientId;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RecipeEntityMapper.class);
        recipeId = UUID.randomUUID();
        ingredientId = UUID.randomUUID();
    }

    @Test
    void entityToDomain_mapsAllFields() {
        IngredientEntity ingredientEntity = new IngredientEntity(ingredientId, "Tomato", "Big", Unit.PIECE, 5);
        RecipeEntity entity = new RecipeEntity(recipeId, "Lasagne", "Good", "http://image", List.of(ingredientEntity));

        Recipe recipe = mapper.entityToDomain(entity);

        assertThat(recipe.getIngredients()).hasSize(1);
        Ingredient ingredient = recipe.getIngredients().get(0);

        assertSoftly(softly -> {
            softly.assertThat(recipe.getId()).isEqualTo(recipeId);
            softly.assertThat(recipe.getName()).isEqualTo("Lasagne");
            softly.assertThat(recipe.getDescription()).isEqualTo("Good");
            softly.assertThat(recipe.getImageUrl()).isEqualTo("http://image");
            softly.assertThat(ingredient.getId()).isEqualTo(ingredientId);
            softly.assertThat(ingredient.getName()).isEqualTo("Tomato");
            softly.assertThat(ingredient.getComment()).isEqualTo("Big");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(ingredient.getAmount()).isEqualTo(5);
        });
    }

    @Test
    void domainToEntity_mapsAllFields() {
        Ingredient ingredient = new Ingredient(ingredientId, "Tomato", "Big", Unit.PIECE, 5);
        Recipe recipe = new Recipe(recipeId, "Lasagne", "Good", "http://image", List.of(ingredient));

        RecipeEntity entity = mapper.domainToEntity(recipe);

        assertThat(entity.getIngredients()).hasSize(1);
        IngredientEntity ingredientEntity = entity.getIngredients().get(0);

        assertSoftly(softly -> {
            softly.assertThat(entity.getId()).isEqualTo(recipeId);
            softly.assertThat(entity.getName()).isEqualTo("Lasagne");
            softly.assertThat(entity.getDescription()).isEqualTo("Good");
            softly.assertThat(entity.getImageUrl()).isEqualTo("http://image");
            softly.assertThat(ingredientEntity.getId()).isEqualTo(ingredientId);
            softly.assertThat(ingredientEntity.getName()).isEqualTo("Tomato");
            softly.assertThat(ingredientEntity.getComment()).isEqualTo("Big");
            softly.assertThat(ingredientEntity.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(ingredientEntity.getAmount()).isEqualTo(5);
        });
    }
}
```


**Vorteil von SoftAssertions:**
- Bei normalen Assertions bricht Test beim ersten fehlgeschlagenen Feld sofort ab - man muss Fehler beheben, Test erneut ausführen und sieht erst dann den nächsten Fehler
- Mit `assertSoftly` alle definierten Feld-Prüfungen (ID, Name, Beschreibung, Zutaten, Einheiten etc.) komplett durchlaufen - Am Ende Übersicht über sämtliche fehlerhaften Felder auf einmal, was Debuggen bei grossen Objekten massiv beschleunigt

<img width="893" height="290" alt="7Passing tests" src="https://github.com/user-attachments/assets/d09faff0-128c-4967-8257-9a0a13dd1b56" />

**Ergebnis:** Alle Unit Tests laufen fehlerfrei durch und validieren Controller-Logik und fehlerfreie Datenkonvertierung der Mapper

## Aufgabe 2 - Automatisierte Reports (JaCoCo & Surefire)

**Ziel:** Neben Testausführung soll automatisiert sichtbarer Test- und Code-Coverage-Report erstellt werden, um nachvollziehbar zu machen, wie viel Prozent des Quellcodes durch Tests abgedeckt sind

**Konfiguration:**
Dafür in `pom.xml` JaCoCo Plugin in Kombination mit dem Maven Surefire Plugin eingerichtet:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

**Ausführung & Ergebnis:**
Nach lokalen Testlauf (`mvn clean test`) werden Reports generiert:
- **Surefire-Testergebnisse:** `target/surefire-reports/`
- **JaCoCo HTML-Coverage-Report:** `target/site/jacoco/index.html`

**Ergebnis:** Report zeigt detailliert an, dass Controller- und Mapper-Klassen erfolgreich durch Unit-Tests abgedeckt wurden

### Code-Coverage Auswertung (JaCoCo Report)
Nach erfolgreichen Testlauf generiert JaCoCo detaillierte Übersicht über Testabdeckung der einzelnen Packages:

<img width="959" height="538" alt="JaCoCo-HTML-Reports" src="https://github.com/user-attachments/assets/aa408a81-4d73-489e-b028-f0ef6c63a800" />

**Interpretation:**
- kritischen, von uns getesteten Komponenten wie **Controller** (`ch.tbz.recipe.planner.controller`) weisen exzellente Testabdeckung von **100%** auf
- Auch **Mapper** stark abgedeckt, wodurch sichergestellt, dass Datenkonvertierung fehlerfrei funktioniert
- (Dass Domain- und Service-Klassen tiefere Abdeckungen haben, liegt daran, dass in dieser Übung primär der Fokus auf den Controller- und Mapper-Tests lag.)

## Aufgabe 3 - CI/CD Pipeline (GitHub Actions)

**Ziel:**
Aufsetzen einer automatisierten Build-Pipeline, die bei jedem `git push` oder Pull Request auf Main-Branch automatisch Projekt baut, Unit-Tests ausführt und Testergebnisse als Artifact bereitstellt.

### Workflow-Konfiguration
Pipeline wurde im Repository unter `.github/workflows/backend-ci.yml` hinterlegt:

```yaml
name: Backend CI

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: CICDPipelines-DeploymentUmgebung/recipe-planner-fronend-and-backend/recipe-planner-backend

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'maven'

      - name: Build and run Unit Tests
        run: mvn --batch-mode clean test

      - name: Upload Test & Coverage Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-and-coverage-reports
          path: |
            CICDPipelines-DeploymentUmgebung/recipe-planner-fronend-and-backend/recipe-planner-backend/target/surefire-reports
            CICDPipelines-DeploymentUmgebung/recipe-planner-fronend-and-backend/recipe-planner-backend/target/site/jacoco
```

### Nachweis

1. Pipeline-Ausführung (GitHub Actions):
- erste Testlauf schlug initial fehl (Konfigurationsanpassung), lief beim zweiten Commit (setup and do Aufgabe 1 and 2) jedoch vollständig grün durch.

<img width="954" height="491" alt="Workflows" src="https://github.com/user-attachments/assets/c9b677d1-0786-425e-a09d-25acc37cb1a9" />

2. Artifacts (Generierte Reports):
Am Ende jedes erfolgreichen Durchlaufs generiert die Pipeline automatisch ein herunterladbares Artifact, das die Surefire- und JaCoCo-Berichte enthält.

<img width="935" height="474" alt="test-and-coverage-reportsArtifactInWorkflow" src="https://github.com/user-attachments/assets/534313f2-5856-40f2-8166-49eecd148773" />

<img width="953" height="484" alt="test-and-coverage-reportsArtifact" src="https://github.com/user-attachments/assets/cdea56c4-f2ce-4b17-b2fd-b2f045ddce4e" />

**Ergebnis:** Jeder Push triggert nun verlässlich Testsuite in frischen Linux-Umgebung, fängt Fehler sofort ab und stellt die Reports zentral zur Verfügung
