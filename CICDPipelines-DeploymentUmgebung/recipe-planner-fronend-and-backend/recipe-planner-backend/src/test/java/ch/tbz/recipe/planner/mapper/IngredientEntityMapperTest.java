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