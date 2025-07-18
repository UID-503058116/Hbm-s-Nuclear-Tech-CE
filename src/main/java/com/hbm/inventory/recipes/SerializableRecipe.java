package com.hbm.inventory.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.MatDistribution;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.util.Tuple;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.*;

import static com.hbm.main.MainRegistry.proxy;

// TODO: I'll have to make most of current recipe handlers extend this
public abstract class SerializableRecipe {
  public static final Gson gson = new Gson();
  public static List<SerializableRecipe> recipeHandlers = new ArrayList<>();

  /*
   * INIT
   */

  public static void registerAllHandlers() {
    recipeHandlers.add(new AmmoPressRecipes());
    recipeHandlers.add(new ArcWelderRecipes());
    recipeHandlers.add(new BreederRecipes());
    recipeHandlers.add(new ChemplantRecipes());
    recipeHandlers.add(new CokerRecipes());
    recipeHandlers.add(new CrackRecipes());
    recipeHandlers.add(new CrucibleRecipes());
    recipeHandlers.add(new CrystallizerRecipes());
    recipeHandlers.add(new FractionRecipes());
    recipeHandlers.add(new HydrotreatingRecipes());
    recipeHandlers.add(new LiquefactionRecipes());
    recipeHandlers.add(new MatDistribution());
    recipeHandlers.add(new MixerRecipes());
    recipeHandlers.add(new RBMKOutgasserRecipes());
    recipeHandlers.add(new ReformingRecipes());
    recipeHandlers.add(new RotaryFurnaceRecipes());
    recipeHandlers.add(new SolderingRecipes());
    recipeHandlers.add(new SolidificationRecipes());
  }

  public static void initialize() {
    File recDir =
        new File(proxy.getDataDir().getPath() + "/config/hbm" + File.separatorChar + "hbmRecipes");

    if (!recDir.exists()) {
      if (!recDir.mkdir()) {
        throw new IllegalStateException(
            "Unable to make recipe directory " + recDir.getAbsolutePath());
      }
    }

    File info =
        new File(
            recDir.getAbsolutePath()
                + File.separatorChar
                + "REMOVE UNDERSCORE TO ENABLE RECIPE LOADING - RECIPES WILL RESET TO DEFAULT OTHERWISE");
    try {
      info.createNewFile();
    } catch (IOException ignored) {
    }

    MainRegistry.logger.info("Starting recipe init!");

    for (SerializableRecipe recipe : recipeHandlers) {

      recipe.deleteRecipes();

      File recFile = new File(recDir.getAbsolutePath() + File.separatorChar + recipe.getFileName());
      if (recFile.exists() && recFile.isFile()) {
        MainRegistry.logger.info("Reading recipe file " + recFile.getName());
        recipe.readRecipeFile(recFile);
      } else {
        MainRegistry.logger.info(
            "No recipe file found, registering defaults for {}", recipe.getFileName());
        recipe.registerDefaults();

        File recTemplate =
            new File(recDir.getAbsolutePath() + File.separatorChar + "_" + recipe.getFileName());
        MainRegistry.logger.info("Writing template file {}", recTemplate.getName());
        recipe.writeTemplateFile(recTemplate);
      }

      recipe.registerPost();
    }

    MainRegistry.logger.info("Finished recipe init!");
  }

  /*
   * ABSTRACT
   */

  /** The machine's (or process') name used for the recipe file */
  public abstract String getFileName();

  /** Return the list object holding all the recipes, usually an ArrayList or HashMap */
  public abstract Object getRecipeObject();

  /**
   * Will use the supplied JsonElement (usually casts to JsonArray) from the over arching recipe
   * array and adds the recipe to the recipe list object
   */
  public abstract void readRecipe(JsonElement recipe);

  /**
   * Is given a single recipe from the recipe list object (a wrapper, Tuple, array, HashMap Entry,
   * etc) and writes it to the current ongoing GSON stream
   *
   * @throws IOException
   */
  public abstract void writeRecipe(Object recipe, JsonWriter writer) throws IOException;

  /** Registers the default recipes */
  public abstract void registerDefaults();

  /** Deletes all existing recipes, currenly unused */
  public abstract void deleteRecipes();

  /**
   * A routine called after registering all recipes, whether it's a template or not. Good for IMC
   * functionality.
   */
  public void registerPost() {}

  /** Returns a string to be printed as info at the top of the JSON file */
  public String getComment() {
    return null;
  }

  /*
   * JSON R/W WRAPPERS
   */

  public void writeTemplateFile(File template) {

    try {
      /* Get the recipe list object */
      Object recipeObject = this.getRecipeObject();
      List<Object> recipeList = new ArrayList<>();

      /* Try to pry all recipes from our list */
      if (recipeObject instanceof Collection) {
        recipeList.addAll((Collection<?>) recipeObject);

      } else if (recipeObject instanceof HashMap) {
        recipeList.addAll(((HashMap<?, ?>) recipeObject).entrySet());
      }

      if (recipeList.isEmpty())
        throw new IllegalStateException(
            "Error while writing recipes for "
                + this.getClass().getSimpleName()
                + ": Recipe list is either empty or in an unsupported format!");

      JsonWriter writer = new JsonWriter(new FileWriter(template));
      writer.setIndent("  "); // pretty formatting
      writer.beginObject(); // initial '{'

      if (this.getComment() != null) {
        writer.name("comment").value(this.getComment());
      }

      writer.name("recipes").beginArray(); // all recipes are stored in an array called "recipes"

      for (Object recipe : recipeList) {
        writer.beginObject(); // begin object for a single recipe
        this.writeRecipe(recipe, writer); // serialize here
        writer.endObject(); // end recipe object
      }

      writer.endArray(); // end recipe array
      writer.endObject(); // final '}'
      writer.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void readRecipeFile(File file) {

    try {
      JsonObject json = gson.fromJson(new FileReader(file), JsonObject.class);
      JsonArray recipes = json.get("recipes").getAsJsonArray();
      for (JsonElement recipe : recipes) {
        this.readRecipe(recipe);
      }
    } catch (FileNotFoundException ignored) {
    }
  }

  /*
   * JSON IO UTIL
   */

  public static RecipesCommon.AStack readAStack(JsonArray array) {
    try {
      String type = array.get(0).getAsString();
      int stacksize = array.size() > 2 ? array.get(2).getAsInt() : 1;
      if ("item".equals(type)) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(array.get(1).getAsString()));
        int meta = array.size() > 3 ? array.get(3).getAsInt() : 0;
        return new RecipesCommon.ComparableStack(item, stacksize, meta);
      }
      if ("dict".equals(type)) {
        String dict = array.get(1).getAsString();
        return new RecipesCommon.OreDictStack(dict, stacksize);
      }
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading stack array {}", array.toString());
    return new RecipesCommon.ComparableStack(ModItems.nothing);
  }

  public static RecipesCommon.AStack[] readAStackArray(JsonArray array) {
    try {
      RecipesCommon.AStack[] items = new RecipesCommon.AStack[array.size()];
      for (int i = 0; i < items.length; i++) {
        items[i] = readAStack((JsonArray) array.get(i));
      }
      return items;
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading stack array {}", array.toString());
    return new RecipesCommon.AStack[0];
  }

  public static void writeAStack(RecipesCommon.AStack astack, JsonWriter writer)
      throws IOException {
    writer.beginArray();
    writer.setIndent("");
    if (astack instanceof RecipesCommon.ComparableStack comp) {
      writer.value("item"); // ITEM  identifier
      writer.value(
          Objects.requireNonNull(Item.REGISTRY.getNameForObject(comp.toStack().getItem()))
              .toString()); // item name
      if (comp.stacksize != 1 || comp.meta > 0) writer.value(comp.stacksize); // stack size
      if (comp.meta > 0) writer.value(comp.meta); // metadata
    }
    if (astack instanceof RecipesCommon.OreDictStack ore) {
      writer.value("dict"); // DICT identifier
      writer.value(ore.name); // dict name
      if (ore.stacksize != 1) writer.value(ore.stacksize); // stacksize
    }
    writer.endArray();
    writer.setIndent("  ");
  }

  public static ItemStack readItemStack(JsonArray array) {
    try {
      Item item = Item.REGISTRY.getObject(new ResourceLocation(array.get(0).getAsString()));
      int stacksize = array.size() > 1 ? array.get(1).getAsInt() : 1;
      int meta = array.size() > 2 ? array.get(2).getAsInt() : 0;
      if (item != null) return new ItemStack(item, stacksize, meta);
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error(
        "Error reading stack array {} - defaulting to NOTHING item!", array.toString());
    return new ItemStack(ModItems.nothing);
  }

  public static Tuple.Pair<ItemStack, Float> readItemStackChance(JsonArray array) {
    try {
      Item item = Item.REGISTRY.getObject(new ResourceLocation(array.get(0).getAsString()));
      int stacksize = array.size() > 2 ? array.get(1).getAsInt() : 1;
      int meta = array.size() > 3 ? array.get(2).getAsInt() : 0;
      float chance = array.get(array.size() - 1).getAsFloat();
      if (item != null) return new Tuple.Pair<>(new ItemStack(item, stacksize, meta), chance);
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error(
        "Error reading stack array {} - defaulting to NOTHING item!", array.toString());
    return new Tuple.Pair<>(new ItemStack(ModItems.nothing), 1F);
  }

  public static ItemStack[] readItemStackArray(JsonArray array) {
    try {
      ItemStack[] items = new ItemStack[array.size()];
      for (int i = 0; i < items.length; i++) {
        items[i] = readItemStack((JsonArray) array.get(i));
      }
      return items;
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading stack array " + array.toString());
    return new ItemStack[0];
  }

  public static Tuple.Pair<ItemStack, Float>[] readItemStackArrayChance(JsonArray array) {
    try {
      Tuple.Pair<ItemStack, Float>[] items = new Tuple.Pair[array.size()];
      for (int i = 0; i < items.length; i++) {
        items[i] = readItemStackChance((JsonArray) array.get(i));
      }
      return items;
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading stack array " + array.toString());
    return new Tuple.Pair[0];
  }

  public static void writeItemStack(ItemStack stack, JsonWriter writer) throws IOException {
    writer.beginArray();
    writer.setIndent("");
    writer.value(
        Objects.requireNonNull(Item.REGISTRY.getNameForObject(stack.getItem()))
            .toString()); // item name
    if (stack.getCount() != 1 || stack.getItemDamage() != 0)
      writer.value(stack.getCount()); // stack size
    if (stack.getItemDamage() != 0) writer.value(stack.getItemDamage()); // metadata
    writer.endArray();
    writer.setIndent("  ");
  }

  public static void writeItemStackChance(Tuple.Pair<ItemStack, Float> stack, JsonWriter writer)
      throws IOException {
    writer.beginArray();
    writer.setIndent("");
    writer.value(
        Objects.requireNonNull(Item.REGISTRY.getNameForObject(stack.getKey().getItem()))
            .toString()); // item name
    if (stack.getKey().getCount() != 1 || stack.getKey().getItemDamage() != 0)
      writer.value(stack.getKey().getCount()); // stack size
    if (stack.getKey().getItemDamage() != 0)
      writer.value(stack.getKey().getItemDamage()); // metadata
    writer.value(stack.getValue()); // chance
    writer.endArray();
    writer.setIndent("  ");
  }

  public static FluidStack readFluidStack(JsonArray array) {
    try {
      FluidType type = Fluids.fromName(array.get(0).getAsString());
      int fill = array.get(1).getAsInt();
      int pressure = array.size() < 3 ? 0 : array.get(2).getAsInt();
      return new FluidStack(type, fill, pressure);
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading fluid array {}", array.toString());
    return null;
  }

  public static FluidStack[] readFluidArray(JsonArray array) {
    try {
      FluidStack[] fluids = new FluidStack[array.size()];
      for (int i = 0; i < fluids.length; i++) {
        fluids[i] = readFluidStack((JsonArray) array.get(i));
      }
      return fluids;
    } catch (Exception ignored) {
    }
    MainRegistry.logger.error("Error reading fluid array {}", array.toString());
    return new FluidStack[0];
  }

  public static void writeFluidStack(FluidStack stack, JsonWriter writer) throws IOException {
    writer.beginArray();
    writer.setIndent("");
    writer.value(stack.type.getName()); // fluid type
    writer.value(stack.fill); // amount in mB
    if (stack.pressure != 0) writer.value(stack.pressure);
    writer.endArray();
    writer.setIndent("  ");
  }

  public static boolean matchesIngredients(ItemStack[] inputs, RecipesCommon.AStack[] recipe) {

    List<RecipesCommon.AStack> recipeList = new ArrayList<>();
    Collections.addAll(recipeList, recipe);

    for (ItemStack inputStack : inputs) {
      if (inputStack != ItemStack.EMPTY) {
        boolean hasMatch = false;

        for (RecipesCommon.AStack recipeStack : recipeList) {
          if (recipeStack.matchesRecipe(inputStack, true)
              && inputStack.getCount() >= recipeStack.stacksize) {
            hasMatch = true;
            recipeList.remove(recipeStack);
            break;
          }
        }
        if (!hasMatch) {
          return false;
        }
      }
    }
    return recipeList.isEmpty();
  }
}
