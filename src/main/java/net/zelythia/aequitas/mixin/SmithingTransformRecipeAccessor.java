package net.zelythia.aequitas.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {

    @Accessor
    abstract Ingredient getTemplate();

    @Accessor
    abstract Ingredient getBase();

    @Accessor
    abstract Ingredient getAddition();

}
