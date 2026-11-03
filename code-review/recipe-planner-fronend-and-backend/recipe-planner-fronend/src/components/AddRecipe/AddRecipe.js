import React, { useState } from "react";
import "./AddRecipe.css";
import { useForm } from "react-hook-form";
import { Form, Button, Col, Row, Alert } from "react-bootstrap";

import axios from "axios";
import AddIngredient from "../AddIngredient/AddIngredient";

// Falls dein Backend nicht auf 8080 läuft, hier den Port anpassen
const API_URL = "http://localhost:8080/api/recipes";

function AddRecipe() {
  const [ingredients, setIngredients] = useState([]);
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    imageUrl: "",
    ingredients: [],
    id: null,
  });

  const [listId, setListId] = useState(1);
  const [message, setMessage] = useState(null);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm();

  const handleChange = (event) => {
    setFormData({ ...formData, [event.target.name]: event.target.value });
  };

  const addIngredient = () => {
    setFormData({
      ...formData,
      ingredients: [
        ...formData.ingredients,
        {
          listId: listId,
          ingredient: "",
          unit: "PIECE",
          quantity: "",
        },
      ],
    });
    setListId(listId + 1);
  };
  const updateIngredient = (ingredientObj) => {
    const updatedIngredients = formData.ingredients.map((ingredient) => {
      if (ingredient.listId === ingredientObj.listId) {
        return ingredientObj;
      }
      return ingredient;
    });
    setFormData({ ...formData, ingredients: updatedIngredients });
  };

  const removeIngredient = (ingredientObj) => {
    const updatedIngredients = formData.ingredients.filter(
      (ingredient) => ingredient.listId !== ingredientObj.listId,
    );
    setFormData({ ...formData, ingredients: updatedIngredients });
  };

  const submitRecipe = (event) => {
    event.preventDefault();

    // Die Felder werden auf das Format des Backends gemappt
    const recipe = {
      name: formData.name,
      description: formData.description,
      imageUrl: formData.imageUrl,
      ingredients: formData.ingredients.map((ing) => ({
        name: ing.ingredient,
        comment: "",
        unit: ing.unit,
        amount: parseInt(ing.quantity, 10) || 0,
      })),
    };

    axios
      .post(API_URL, recipe)
      .then(() => {
        setMessage({ type: "success", text: "Recipe wurde gespeichert." });
        setFormData({
          name: "",
          description: "",
          imageUrl: "",
          ingredients: [],
          id: null,
        });
      })
      .catch(() => {
        setMessage({ type: "danger", text: "Speichern fehlgeschlagen." });
      });
  };

  const renderIngredients = formData.ingredients.map((ingredient) => (
    <AddIngredient
      key={ingredient.listId}
      ingredient={ingredient}
      ingredients={ingredients}
      listId={listId - 1}
      updateIngredient={updateIngredient}
      removeIngredient={removeIngredient}
    />
  ));

  return (
    <>
      <div className="bg">
        <Form className="m-3" onSubmit={submitRecipe}>
          <h1 className="h3 bg-dark text-bg-primary mt-2">Add Recipe</h1>
          {message && <Alert variant={message.type}>{message.text}</Alert>}
          <Form.Group className="mb-1" controlId="formBasicName">
            <Form.Label>Recipe Name:</Form.Label>
            <Form.Control
              name="name"
              placeholder="Name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-1" controlId="formBasicDescription">
            <Form.Label>Description:</Form.Label>
            <Form.Control
              name="description"
              placeholder="Description"
              value={formData.description}
              onChange={handleChange}
            />
          </Form.Group>
          <Form.Group className="mb-1 mb-5" controlId="formBasicImageUrl">
            <Form.Label>Image URL:</Form.Label>
            <Form.Control
              name="imageUrl"
              placeholder="URL"
              value={formData.imageUrl}
              onChange={handleChange}
            />
          </Form.Group>
          <Row>
            <Col>Ingredient</Col>
            <Col>Unit</Col>
            <Col>Quanity</Col>
            <Col xs={1}></Col>
          </Row>
          <hr />
          <Row>
            <br></br>
          </Row>
          {renderIngredients}
          <Row>
            <br></br>
            <Button variant="warning" onClick={addIngredient} className="mt-1">
              Add Ingredient
            </Button>
          </Row>
          <Button variant="primary" type="submit" className="mb-5">
            Submit
          </Button>
        </Form>
      </div>
    </>
  );
}

export default AddRecipe;
