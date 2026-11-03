import React from "react";
import PropTypes from "prop-types";
import "./AddIngredient.css";
import { Button, Col, Form, Row } from "react-bootstrap";

const AddIngredient = ({ ingredient, updateIngredient, removeIngredient }) => {
  const handleChange = (event) => {
    updateIngredient({
      ...ingredient,
      [event.target.name]: event.target.value,
    });
  };

  return (
    <Row>
      <Col>
        <Form.Group className="mb-1">
          <Form.Control
            name="ingredient"
            placeholder="Name"
            value={ingredient.ingredient}
            onChange={handleChange}
          />
        </Form.Group>
      </Col>
      <Col>
        <Form.Group className="mb-1">
          <Form.Select
            name="unit"
            value={ingredient.unit}
            onChange={handleChange}
          >
            <option>PIECE</option>
            <option>GRAMM</option>
            <option>KILOGRAMM</option>
            <option>LITRE</option>
            <option>DECILITRE</option>
          </Form.Select>
        </Form.Group>
      </Col>
      <Col>
        <Form.Group className="mb-1">
          <Form.Control
            name="quantity"
            type="number"
            min="0"
            placeholder="Quantity"
            value={ingredient.quantity}
            onChange={handleChange}
          />
        </Form.Group>
      </Col>
      <Col xs={1}>
        <Button
          onClick={(e) => removeIngredient(ingredient)}
          variant="outline-dark"
          className="mb-1"
        >
          x
        </Button>
      </Col>
    </Row>
  );
};

AddIngredient.propTypes = {};

AddIngredient.defaultProps = {};

export default AddIngredient;
