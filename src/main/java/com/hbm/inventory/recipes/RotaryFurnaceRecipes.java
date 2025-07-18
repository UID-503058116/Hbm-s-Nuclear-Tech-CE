package com.hbm.inventory.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.RecipesCommon.*;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.items.machine.ItemScraps;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.*;

import static com.hbm.inventory.OreDictManager.*;
import static com.hbm.inventory.material.MaterialShapes.*;
import static com.hbm.inventory.material.Mats.*;

public class RotaryFurnaceRecipes extends SerializableRecipe {

  public static List<RotaryFurnaceRecipe> recipes = new ArrayList<>();

  @Override
  public void registerDefaults() {

    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_STEEL, INGOT.q(1)),
            100,
            100,
            new OreDictStack(IRON.ingot()),
            new OreDictStack(COAL.gem())));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_STEEL, INGOT.q(1)),
            100,
            100,
            new OreDictStack(IRON.ingot()),
            new OreDictStack(ANY_COKE.gem())));

    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_STEEL, INGOT.q(2)),
            200,
            25,
            new OreDictStack(IRON.fragment(), 9),
            new OreDictStack(COAL.gem())));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_STEEL, INGOT.q(3)),
            200,
            25,
            new OreDictStack(IRON.fragment(), 9),
            new OreDictStack(ANY_COKE.gem())));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_STEEL, INGOT.q(4)),
            400,
            25,
            new OreDictStack(IRON.fragment(), 9),
            new OreDictStack(ANY_COKE.gem()),
            new ComparableStack(ModItems.powder_flux)));

    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_DESH, INGOT.q(1)),
            100,
            200,
            new FluidStack(Fluids.LIGHTOIL, 100),
            new ComparableStack(ModItems.powder_desh_ready)));

    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_GUNMETAL, INGOT.q(4)),
            200,
            100,
            new OreDictStack(CU.ingot(), 3),
            new OreDictStack(AL.ingot(), 1)));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_WEAPONSTEEL, INGOT.q(1)),
            200,
            400,
            new FluidStack(Fluids.GAS_COKER, 100),
            new OreDictStack(STEEL.ingot(), 1),
            new ComparableStack(ModItems.powder_flux, 2)));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_SATURN, INGOT.q(2)),
            200,
            400,
            new FluidStack(Fluids.REFORMGAS, 250),
            new OreDictStack(DURA.dust(), 4),
            new OreDictStack(CU.dust())));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_SATURN, INGOT.q(4)),
            200,
            300,
            new FluidStack(Fluids.REFORMGAS, 250),
            new OreDictStack(DURA.dust(), 4),
            new OreDictStack(CU.dust()),
            new OreDictStack(BORAX.dust())));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_ALUMINIUM, INGOT.q(2)),
            100,
            400,
            new FluidStack(Fluids.SODIUM_ALUMINATE, 150)));
    recipes.add(
        new RotaryFurnaceRecipe(
            new MaterialStack(MAT_ALUMINIUM, INGOT.q(3)),
            40,
            200,
            new FluidStack(Fluids.SODIUM_ALUMINATE, 150),
            new ComparableStack(ModItems.powder_flux, 2)));
  }

  public static HashMap<Object, Object> getRecipes() {

    HashMap<Object, Object> recipes = new HashMap<>();

    for (RotaryFurnaceRecipe recipe : RotaryFurnaceRecipes.recipes) {

      int size = recipe.ingredients.length + (recipe.fluid != null ? 1 : 0);
      Object[] array = new Object[size];

      System.arraycopy(recipe.ingredients, 0, array, 0, recipe.ingredients.length);

      if (recipe.fluid != null) array[size - 1] = ItemFluidIcon.make(recipe.fluid);

      recipes.put(array, ItemScraps.create(recipe.output, true));
    }

    return recipes;
  }

  public static RotaryFurnaceRecipe getRecipe(ItemStack... inputs) {

    outer:
    for (RotaryFurnaceRecipe recipe : recipes) {

      List<AStack> recipeList = new ArrayList<>(Arrays.asList(recipe.ingredients));

      for (ItemStack inputStack : inputs) {

        if (inputStack != null) {

          boolean hasMatch = false;

          for (AStack recipeStack : recipeList) {
            if (recipeStack.matchesRecipe(inputStack, true)
                && inputStack.getCount() >= recipeStack.stacksize) {
              hasMatch = true;
              recipeList.remove(recipeStack);
              break;
            }
          }

          if (!hasMatch) {
            continue outer;
          }
        }
      }

      if (recipeList.isEmpty()) return recipe;
    }

    return null;
  }

  @Override
  public String getFileName() {
    return "hbmRotaryFurnace.json";
  }

  @Override
  public Object getRecipeObject() {
    return recipes;
  }

  @Override
  public void deleteRecipes() {
    recipes.clear();
  }

  @Override
  public void readRecipe(JsonElement recipe) {
    JsonObject obj = (JsonObject) recipe;

    AStack[] inputs = readAStackArray(obj.get("inputs").getAsJsonArray());
    FluidStack fluid = obj.has("fluid") ? readFluidStack(obj.get("fluid").getAsJsonArray()) : null;

    JsonArray array = obj.get("output").getAsJsonArray();
    Mats.MaterialStack stack =
        new MaterialStack(Mats.matByName.get(array.get(0).getAsString()), array.get(1).getAsInt());

    int duration = obj.get("duration").getAsInt();
    int steam = obj.get("steam").getAsInt();

    recipes.add(new RotaryFurnaceRecipe(stack, duration, steam, fluid, inputs));
  }

  @Override
  public void writeRecipe(Object obj, JsonWriter writer) throws IOException {
    RotaryFurnaceRecipe recipe = (RotaryFurnaceRecipe) obj;

    writer.name("inputs").beginArray();
    for (AStack aStack : recipe.ingredients) {
      writeAStack(aStack, writer);
    }
    writer.endArray();

    if (recipe.fluid != null) {
      writer.name("fluid");
      writeFluidStack(recipe.fluid, writer);
    }

    writer.name("output").beginArray();
    writer.setIndent("");
    writer.value(recipe.output.material.names[0]).value(recipe.output.amount);
    writer.endArray();
    writer.setIndent("  ");

    writer.name("duration").value(recipe.duration);
    writer.name("steam").value(recipe.steam);
  }

  public static class RotaryFurnaceRecipe {

    public AStack[] ingredients;
    public FluidStack fluid;
    public MaterialStack output;
    public int duration;
    public int steam;

    public RotaryFurnaceRecipe(
        MaterialStack output, int duration, int steam, FluidStack fluid, AStack... ingredients) {
      this.ingredients = ingredients;
      this.fluid = fluid;
      this.output = output;
      this.duration = duration;
      this.steam = steam;
    }

    public RotaryFurnaceRecipe(
        MaterialStack output, int duration, int steam, AStack... ingredients) {
      this(output, duration, steam, null, ingredients);
    }
  }
}
