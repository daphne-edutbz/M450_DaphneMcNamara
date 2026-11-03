import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Form, Button, Alert } from "react-bootstrap";
import axios from "axios";

// Falls dein Backend nicht auf 8080 läuft, hier den Port anpassen
const API_URL = "http://localhost:8080/api/recipes";

function EditRecipe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    axios
      .get(`${API_URL}/recipe/${id}`)
      .then((response) => {
        // Das Backend gibt bei unbekannter ID 200 mit leerem Body zurück
        if (response.data) {
          setRecipe(response.data);
        } else {
          setMessage({ type: "danger", text: "Recipe wurde nicht gefunden." });
        }
      })
      .catch(() =>
        setMessage({
          type: "danger",
          text: "Recipe konnte nicht geladen werden.",
        }),
      );
  }, [id]);

  const handleChange = (event) => {
    setRecipe({ ...recipe, [event.target.name]: event.target.value });
  };

  const submitRecipe = (event) => {
    event.preventDefault();
    axios
      .put(`${API_URL}/${id}`, recipe)
      .then(() => navigate("/"))
      .catch(() =>
        setMessage({ type: "danger", text: "Speichern fehlgeschlagen." }),
      );
  };

  if (!recipe) {
    return message ? (
      <Alert variant={message.type} className="m-3">
        {message.text}
      </Alert>
    ) : null;
  }

  return (
    <div className="bg">
      <Form className="m-3" onSubmit={submitRecipe}>
        <h1 className="h3 bg-dark text-bg-primary mt-2">Edit Recipe</h1>
        {message && <Alert variant={message.type}>{message.text}</Alert>}
        <Form.Group className="mb-1" controlId="editName">
          <Form.Label>Recipe Name:</Form.Label>
          <Form.Control
            name="name"
            value={recipe.name}
            onChange={handleChange}
            required
          />
        </Form.Group>
        <Form.Group className="mb-1" controlId="editDescription">
          <Form.Label>Description:</Form.Label>
          <Form.Control
            name="description"
            value={recipe.description}
            onChange={handleChange}
          />
        </Form.Group>
        <Form.Group className="mb-3" controlId="editImageUrl">
          <Form.Label>Image URL:</Form.Label>
          <Form.Control
            name="imageUrl"
            value={recipe.imageUrl}
            onChange={handleChange}
          />
        </Form.Group>
        <h2 className="h5">Ingredients (nicht bearbeitbar)</h2>
        <ul>
          {(recipe.ingredients || []).map((ing) => (
            <li key={ing.id}>
              {ing.amount} {ing.unit} {ing.name}
            </li>
          ))}
        </ul>
        <Button variant="primary" type="submit" className="mb-5">
          Save
        </Button>
      </Form>
    </div>
  );
}

export default EditRecipe;
