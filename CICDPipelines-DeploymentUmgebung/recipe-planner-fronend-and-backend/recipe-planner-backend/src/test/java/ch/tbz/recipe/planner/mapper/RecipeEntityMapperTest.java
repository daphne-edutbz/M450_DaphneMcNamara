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