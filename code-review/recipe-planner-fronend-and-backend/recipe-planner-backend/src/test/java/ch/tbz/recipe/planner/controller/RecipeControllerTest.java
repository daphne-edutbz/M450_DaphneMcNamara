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