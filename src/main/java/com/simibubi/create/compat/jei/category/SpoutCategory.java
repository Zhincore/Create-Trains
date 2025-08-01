package com.simibubi.create.compat.jei.category;

import java.util.Collection;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@ParametersAreNonnullByDefault
public class SpoutCategory extends CreateRecipeCategory<FillingRecipe> {

	private final AnimatedSpout spout = new AnimatedSpout();

	public SpoutCategory(Info<FillingRecipe> info) {
		super(info);
	}

	public static void consumeRecipes(Consumer<FillingRecipe> consumer, IIngredientManager ingredientManager) {
		Collection<FluidStack> fluidStacks = ingredientManager.getAllIngredients(ForgeTypes.FLUID_STACK);
		for (ItemStack stack : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
			if (PotionFluidHandler.isPotionItem(stack)) {
				FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(stack);
				Ingredient bottle = Ingredient.of(Items.GLASS_BOTTLE);
				consumer.accept(new ProcessingRecipeBuilder<>(FillingRecipe::new, Create.asResource("potions"))
					.withItemIngredients(bottle)
					.withFluidIngredients(FluidIngredient.fromFluidStack(fluidFromPotionItem))
					.withSingleItemOutput(stack)
					.build());
				continue;
			}

			LazyOptional<IFluidHandlerItem> capability =
				stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (!capability.isPresent())
				continue;

			var existingFluidHandler = capability.orElse(null);
			int numTanks = existingFluidHandler.getTanks();
			FluidStack existingFluid = numTanks == 1 ? existingFluidHandler.getFluidInTank(0) : FluidStack.EMPTY;

			for (FluidStack fluidStack : fluidStacks) {
				// Hoist the fluid equality check to avoid the work of copying the stack + populating capabilities
				// when most fluids will not match
				if (numTanks == 1 && (!existingFluid.isEmpty() && !existingFluid.isFluidEqual(fluidStack))) {
					continue;
				}
				ItemStack copy = stack.copy();
				copy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
					.ifPresent(fhi -> {
						if (!GenericItemFilling.isFluidHandlerValid(copy, fhi))
							return;
						FluidStack fluidCopy = fluidStack.copy();
						fluidCopy.setAmount(1000);
						fhi.fill(fluidCopy, FluidAction.EXECUTE);
						ItemStack container = fhi.getContainer();
						if (ItemHelper.sameItem(container, copy))
							return;
						if (container.isEmpty())
							return;

						Ingredient bucket = Ingredient.of(stack);
						ResourceLocation itemName = CatnipServices.REGISTRIES.getKeyOrThrow(stack.getItem());
						ResourceLocation fluidName = CatnipServices.REGISTRIES.getKeyOrThrow(fluidCopy.getFluid());
						consumer.accept(new ProcessingRecipeBuilder<>(FillingRecipe::new,
							Create.asResource("fill_" + itemName.getNamespace() + "_" + itemName.getPath()
								+ "_with_" + fluidName.getNamespace() + "_" + fluidName.getPath()))
									.withItemIngredients(bucket)
									.withFluidIngredients(FluidIngredient.fromFluidStack(fluidCopy))
									.withSingleItemOutput(container)
									.build());
					});
			}
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FillingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 27, 51)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		addFluidSlot(builder, 27, 32, recipe.getRequiredFluid());

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 132, 51)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(getResultItem(recipe));
	}

	@Override
	public void draw(FillingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);
		spout.withFluids(recipe.getRequiredFluid()
			.getMatchingFluidStacks())
			.draw(graphics, getBackground().getWidth() / 2 - 13, 22);
	}

}
