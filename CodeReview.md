# Code Review - Aufgaben


- Hab die Aufgaben alleine gemacht, deshalb Self-Review: Feature selber implementiert, Pull Request erstellt und den Code danach als Reviewerin kommentiert.


## Aufgabe 1 - Neues Recipe hinzufügen (Frontend)

### Ausgangslage
`AddRecipe.js` war am Anfang nur Optik: Felder nicht mit State verbunden, kein `<Form>`, nichts wurde ans Backend geschickt.

### Feature-Branch erstellen

``` bash
git checkout -b feature/add-recipe
```

<img width="734" height="304" alt="NewFeatBranch" src="https://github.com/user-attachments/assets/39335025-783a-4488-baa2-a985da972ae9" />

### Umsetzung

Änderungen in `src/components/AddRecipe/AddRecipe.js`:
- Felder Name, Description und Image URL mit State verbunden (`name`, `value`, `onChange`)
- Alles in `<Form onSubmit>` gepackt
- Neue Funktion `submitRecipe`, die ein `POST` an `/api/recipes` schickt
- Erfolgs- bzw. Fehlermeldung über `Alert`

```javascript
const submitRecipe = (event) => {
    event.preventDefault()

    // Felder werden auf das Format des Backends gemappt
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

Da ich alleine arbeite, hab ich den Code selber als Reviewerin durchgeschaut (Tab "Files changed") und anhand der Punkte aus der Theorie kommentiert.

Kommentar direkt im Code:

<img width="959" height="527" alt="CommentInCode" src="https://github.com/user-attachments/assets/d73702b1-f14f-4896-8721-d57cb9fcbfd9" />

Formulierter Kommentar:

<img width="953" height="526" alt="FormuliertesKommentar" src="https://github.com/user-attachments/assets/589ffe3f-4619-456c-b54b-1a7c045d3f7c" />

Abgeschlossenes Review mit allen Kommentaren:

<img width="697" height="366" alt="Commented" src="https://github.com/user-attachments/assets/b0ca698b-e5ac-4729-988c-7732a12a22a1" />

**Gefundene Punkte (nach Checkliste aus der Theorie):**

| Punkt aus Theorie | Gefunden |
|---|---|
| Unnötiger Code | `useForm`, `watch`, `errors` und `ingredients` werden importiert bzw. deklariert, aber nie benutzt |
| Typos | Label "Quanity" statt "Quantity" |
| Logik | Leere Menge wird mit `parseInt(...) \|\| 0` still zu 0, keine Validierung |
| Logik | `comment` wird immer leer geschickt, es gibt kein Eingabefeld dafür |
| Fehlerbehandlung | "Speichern fehlgeschlagen" sagt nicht, warum es fehlgeschlagen ist |
| Tests | Für die Komponente gibt es keine Tests |
| SRP | Eine Komponente macht Formular, State-Handling und API-Aufruf gleichzeitig |

### Erkenntnisse

- Beim Selber-Reviewen fallen einem Sachen auf, die man beim Schreiben übersieht (zb. die ungenutzten Imports und der Tippfehler)
- Ein Review mit einer zweiten Person wäre aussagekräftiger, weil man den eigenen Code schnell als "passt schon" ansieht
- Kleiner Pull Request (nur eine Datei) war gut zu überblicken
- Feedback sollte konstruktiv sein und als Vorschlag formuliert werden ("Das wäre auch eine Möglichkeit")

## Aufgabe 2 - Recipe editieren (Frontend und Backend)

