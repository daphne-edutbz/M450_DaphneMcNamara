# Code Review - Aufgaben


- Hab die Aufgaben alleine gemacht, deshalb Self-Review: Feature selber implementiert, Pull Request erstellt und den Code danach als Reviewerin kommentiert.


## Aufgabe 1 - Neues Recipe hinzufügen (Frontend)

### Ausgangslage
`AddRecipe.js` am Anfang nur Optik: Felder nicht mit State verbunden, kein `<Form>`, nichts wurde ans Backend geschickt

### Feature-Branch erstellen

``` bash
git checkout -b feature/add-recipe
```

<img width="734" height="304" alt="NewFeatBranch" src="https://github.com/user-attachments/assets/39335025-783a-4488-baa2-a985da972ae9" />

### Umsetzung

Änderungen in `src/components/AddRecipe/AddRecipe.js`:
- Felder Name, Description und Image URL mit State verbunden (`name`, `value`, `onChange`)
- Alles in `<Form onSubmit>` gepackt
- Neue Funktion `submitRecipe`, die `POST` an `/api/recipes` schickt
- Erfolgs- bzw. Fehlermeldung über `Alert`

```javascript
const submitRecipe = (event) => {
    event.preventDefault()

    // Felder auf Format des Backends gemappt
    const recipe = {
        name: formData.name,
        description: formData.description,
        imageUrl: formData.imageUrl,
        ingredients: formData.ingredients.map((ing) => ({
            name: ing.ingredient,
            comment: '',
            unit: ing.unit,
            amount: parseInt(ing.quantity, 10) || 0
        }))
    }

    axios.post(API_URL, recipe)
        .then(() => {
            setMessage({type: 'success', text: 'Recipe wurde gespeichert.'})
            setFormData({name: '', description: '', imageUrl: '', ingredients: [], id: null})
        })
        .catch(() => {
            setMessage({type: 'danger', text: 'Speichern fehlgeschlagen.'})
        })
}
```

### Testen

Formular ausgefüllt:

<img width="959" height="532" alt="FillFormNewFeature" src="https://github.com/user-attachments/assets/45da78a7-6f84-48ae-ab16-978db200dcf0" />

Nach Submit kommt die Bestätigung:

<img width="956" height="527" alt="FillFormConfirmation" src="https://github.com/user-attachments/assets/a1a2bb20-721a-452e-88d0-d99b75f3c880" />

Neues Recipe erscheint in der Browse-Ansicht:

<img width="959" height="524" alt="NewRecipeInBrowse" src="https://github.com/user-attachments/assets/5af9df0f-50c4-44b4-9984-714b20cd7649" />

### Committen und pushen

```
git add .
git commit -m "Add form to create a new recipe"
git push --set-upstream origin feature/add-recipe
```

<img width="868" height="410" alt="addNewFeature" src="https://github.com/user-attachments/assets/f61a0020-61cb-4b2f-9e4f-e8cc80db3778" />

### Pull Request

Pull Request erstellt von `feature/add-recipe` nach `main`:

<img width="941" height="487" alt="OpenPullRequest" src="https://github.com/user-attachments/assets/8fc7587c-a81c-42b5-818b-0f1f66efd6d5" />

<img width="955" height="572" alt="CreatedPullRequest" src="https://github.com/user-attachments/assets/1ef95bbc-4297-4834-853a-58ff4e941aba" />

Pull Request im Repo:

<img width="950" height="482" alt="PullRequestOnRepo" src="https://github.com/user-attachments/assets/3aa1dc4c-6e58-49ea-9d41-db4d1e19b0f3" />



### Review

Da ich alleine arbeite, hab ich den Code selber als Reviewerin durchgeschaut (Tab "Files changed") und anhand Punkte aus Theorie kommentiert.

Kommentar direkt im Code:

<img width="959" height="527" alt="CommentInCode" src="https://github.com/user-attachments/assets/d73702b1-f14f-4896-8721-d57cb9fcbfd9" />

Formulierter Kommentar:

<img width="953" height="526" alt="FormuliertesKommentar" src="https://github.com/user-attachments/assets/589ffe3f-4619-456c-b54b-1a7c045d3f7c" />

Abgeschlossenes Review mit allen Kommentaren:

<img width="697" height="366" alt="Commented" src="https://github.com/user-attachments/assets/b0ca698b-e5ac-4729-988c-7732a12a22a1" />

**Gefundene Punkte (nach Checkliste aus der Theorie):**

| Punkt aus Theorie | Gefunden |
|---|---|
| Unnötiger Code | `useForm`, `watch`, `errors` und `ingredients` importiert bzw. deklariert, aber nie benutzt |
| Typos | Label "Quanity" statt "Quantity" |
| Logik | Leere Menge wird mit `parseInt(...) \|\| 0` still zu 0, keine Validierung |
| Logik | `comment` immer leer geschickt, es gibt kein Eingabefeld dafür |
| Fehlerbehandlung | "Speichern fehlgeschlagen" sagt nicht, warum es fehlgeschlagen ist |
| Tests | Für die Komponente gibt es keine Tests |
| SRP | Eine Komponente macht Formular, State-Handling und API-Aufruf gleichzeitig |

### Erkenntnisse

- Beim Selber-Reviewen fallen einem Sachen auf, die man beim Schreiben übersieht (zb. die ungenutzten Imports und der Tippfehler)
- Ein Review mit einer zweiten Person wäre aussagekräftiger, weil man den eigenen Code schnell als "passt schon" ansieht
- Kleiner Pull Request (nur eine Datei) war gut zu überblicken
- Feedback sollte konstruktiv sein und als Vorschlag formuliert werden ("Das wäre auch eine Möglichkeit")

## Aufgabe 2 - Recipe editieren (Frontend und Backend)

Backend bisher nur `GET` (alle), `GET` (einzeln) und `POST`, aber keinen Endpoint zum Ändern. Deshalb zwei getrennte Branches und zwei Pull Requests:
- `feature/edit-recipe-backend` -> neuer `PUT`-Endpoint
- `feature/edit-recipe-frontend` -> Formular zum Editieren

Backend zuerst, damit Frontend danach etwas hat, womit es sprechen kann

### Backend

#### Feature-Branch erstellen

```
git checkout -b feature/edit-recipe-backend
```

<img width="860" height="94" alt="Aufgabe2NewBrancg" src="https://github.com/user-attachments/assets/0d5ce7e0-513e-4b45-afdc-d8fac586eed4" />

#### Umsetzung

Im `RecipeService` neue Methode `updateRecipe`:

```java
public Recipe updateRecipe(UUID recipeId, Recipe recipe) {
    if (repository.findById(recipeId).isEmpty()) {
        return null;
    }
    recipe.setId(recipeId);
    var updated = repository.save(mapper.domainToEntity(recipe));
    return mapper.entityToDomain(updated);
}
```

- `findById` statt `existsById`, weil Repository mit `Long` als ID-Typ deklariert ist, die Entity aber eine `UUID` hat
- ID aus Pfad gesetzt, damit nicht ausversehen neues Recipe angelegt wird

Im `RecipeController` neuer Endpoint:

```java
@PutMapping("/api/recipes/{recipeId}")
public ResponseEntity<Recipe> updateRecipe(@PathVariable UUID recipeId, @RequestBody Recipe recipe) {
    Recipe updated = service.updateRecipe(recipeId, recipe);
    if (updated == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(updated, HttpStatus.OK);
}
```

- Unbekannte ID gibt `404` statt `200` mit leerem Body (beim bestehenden `GET` ist das anders)

#### Tests

Zwei neue Tests im `RecipeControllerTest`, einer für bekannte ID (`200`) und einer für unbekannte ID (`404`):

```java
@Test
void updateRecipe_existingId_returns200() throws Exception {
    when(service.updateRecipe(eq(recipeId), any(Recipe.class))).thenReturn(recipe);

    mockMvc.perform(put("/api/recipes/{id}", recipeId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipe)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Lasagne"));
}

@Test
void updateRecipe_unknownId_returns404() throws Exception {
    when(service.updateRecipe(eq(recipeId), any(Recipe.class))).thenReturn(null);

    mockMvc.perform(put("/api/recipes/{id}", recipeId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipe)))
            .andExpect(status().isNotFound());
}
```

Tests im Backend laufen fehlerfrei durch:

<img width="959" height="563" alt="TestsImBackendFehlerfreiAmLaufen" src="https://github.com/user-attachments/assets/dc030ad2-e5ba-4911-bb93-7488c7c18801" />

#### Committen und pushen

```
git add .
git commit -m "Add PUT endpoint to edit a recipe"
git push --set-upstream origin feature/edit-recipe-backend
```

<img width="811" height="275" alt="CommitBackend" src="https://github.com/user-attachments/assets/ed058d41-f7c2-4323-9650-016ea4cabf2e" />

#### Pull Request und Review

<img width="959" height="530" alt="BackendPullRequest" src="https://github.com/user-attachments/assets/c2ebfe50-a5ff-4515-ac0b-d8d4282c1b94" />


Self-Review im Tab "Files changed":

<img width="958" height="479" alt="CommentAufgave2Backend" src="https://github.com/user-attachments/assets/c5c561e6-33c1-4879-87ff-181220a387af" />

**Gefundene Punkte:**

| Punkt aus Theorie | Gefunden |
|---|---|
| Logik | `updateRecipe` im Service gibt `null` zurück, wenn nichts gefunden, Exception oder `Optional` wäre sauberer |
| Performance | `findById` und danach `save` sind zwei Datenbankzugriffe für eine Änderung |
| Logik | Keine Validierung des Bodys, zb. leerer Name wird einfach gespeichert |
| Tests | Service hat keine eigenen Tests, nur Controller |
| Konsistenz | `GET` gibt bei unbekannter ID `200` mit leerem Body, `PUT` gibt `404`, das ist uneinheitlich |

### Frontend

#### Feature-Branch erstellen

```
git checkout -b feature/edit-recipe-frontend
```

<img width="792" height="176" alt="Aufgabe2NewBranchFrontend" src="https://github.com/user-attachments/assets/21e7e9e0-06f5-40d5-bd2a-4ae35285633e" />


#### Umsetzung

Frontend braucht Formular, das bestehendes Recipe lädt und per `PUT` speichert. Zutaten werden angezeigt, aber nicht bearbeitet, damit Pull Request klein bleibt (laut Theorie sind kleine Pull Requests besser reviewbar)

Änderungen:
- Neue Komponente `src/components/EditRecipe/EditRecipe.js`
- `App.js`: neue Route `/edit/:id`
- `Recipe.js`: Button "Edit Details" ist jetzt ein Link auf `/edit/{id}`
- `Browse.js`: gibt die `id` an `Recipe` weiter

Recipe laden und speichern in `EditRecipe.js`:

```javascript
useEffect(() => {
    axios.get(`${API_URL}/recipe/${id}`)
        .then((response) => {
            // Das Backend gibt bei unbekannter ID 200 mit leerem Body zurück
            if (response.data) {
                setRecipe(response.data)
            } else {
                setMessage({type: 'danger', text: 'Recipe wurde nicht gefunden.'})
            }
        })
        .catch(() => setMessage({type: 'danger', text: 'Recipe konnte nicht geladen werden.'}))
}, [id])

const submitRecipe = (event) => {
    event.preventDefault()
    axios.put(`${API_URL}/${id}`, recipe)
        .then(() => navigate('/'))
        .catch(() => setMessage({type: 'danger', text: 'Speichern fehlgeschlagen.'}))
}
```

Neue Route in `App.js`:

```jsx
<Route path="/edit/:id" element={<EditRecipe/>} />
```

Edit-Button in `Recipe.js` als Link:

```jsx
<Button as={Link} to={`/edit/${props.id}`} variant="primary">Edit Details</Button>
```

#### Testen

Editieren jetzt möglich, Formular ist mit bestehenden Recipe vorausgefüllt:

<img width="959" height="532" alt="EditRecipePossibleNow" src="https://github.com/user-attachments/assets/e321ebdf-a6da-4851-92fb-8ebb0ce10c88" />

Nach dem Speichern ist der Name geändert:

<img width="958" height="528" alt="RecipeNameChanged" src="https://github.com/user-attachments/assets/dea345d0-6daa-41a9-9c5b-b1043cc975c3" />

#### Committen und pushen

```
git add .
git commit -m "Add edit form for recipes"
git push --set-upstream origin feature/edit-recipe-frontend
```

<img width="861" height="453" alt="Aufgabe2CommitFrontend" src="https://github.com/user-attachments/assets/13c3bb44-3b44-4240-a2f9-2696bc741926" />


#### Pull Request und Review

<img width="953" height="527" alt="Aufgabe2PullRequestFrontend" src="https://github.com/user-attachments/assets/4cad1522-6b52-423b-993f-0903868666b0" />


Self-Review im Tab "Files changed":

<img width="956" height="531" alt="CommentAufgabe2Frontend" src="https://github.com/user-attachments/assets/45889edc-81c8-46c5-b9cd-5a4acd78d273" />

**Gefundene Punkte:**

| Punkt aus Theorie | Gefunden |
|---|---|
| DRY | API-URL steht jetzt in mehreren Dateien (`Browse.js`, `AddRecipe.js`, `EditRecipe.js`), besser an einer zentralen Stelle |
| Logik | Im `map` in `Browse.js` fehlt ein `key` |
| Vollständigkeit | Zutaten sind im Edit-Formular nicht bearbeitbar, sinnvoll als eigener Follow-up-Pull-Request |
| Fehlerbehandlung | Fehlermeldungen sagen nicht, warum etwas fehlgeschlagen ist |
| Tests | Keine Tests für neue Komponente |

### Erkenntnisse Aufgabe 2
- Zwei getrennte Pull Requests (Backend und Frontend) gut, weil jeder für sich klein und einzeln reviewbar blieb
- Reihenfolge wichtig: Backend musste zuerst da sein, sonst kann Frontend nicht gegen den neuen Endpoint testen
- Beim Review Uneinheitlichkeiten aufgefallen, die beim Schreiben nicht aufgefallen (zb. `404` beim `PUT`, aber `200` mit leerem Body beim `GET`)
- Vorschläge, die über Feature hinausgehen (Zutaten editieren, zentrale API-URL), gehören laut Theorie in einen separaten Follow-up-Pull-Request und nicht in diesen

## Fazit
- Review lohnt sich auch bei kleinen Änderungen, weil man den Code nochmal mit anderen Augen liest
- Allein ist ein Review weniger wertvoll als zu zweit, weil einem die eigenen Denkfehler schwerer auffallen
- Kleine Pull Requests und konstruktive, als Vorschlag formulierte Kommentare machen den Prozess angenehmer
