package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider.GeneratedRecipe;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// From https://github.com/Layers-of-Railways/Railway/blob/1.20/dev/common/src/main/java/com/railwayteam/railways/base/data/recipe/DyedRecipeList.java
public class CRDyedRecipeList implements Iterable<GeneratedRecipe> {

	private static final int COLOR_AMOUNT = DyeColor.values().length;

	protected final GeneratedRecipe[] values =
		new GeneratedRecipe[getColorCount()];

	public CRDyedRecipeList(
		Function<@NotNull DyeColor, GeneratedRecipe> filler
	) {
		for (DyeColor color : DyeColor.values()) {
			values[color.ordinal()] = filler.apply(color);
		}
	}

	protected int getColorCount() {
		return COLOR_AMOUNT;
	}

	public GeneratedRecipe get(@NotNull DyeColor color) {
		return values[color.ordinal()];
	}

	public GeneratedRecipe[] toArray() {
		return Arrays.copyOf(values, values.length);
	}

	@Override
	public Iterator<GeneratedRecipe> iterator() {
		return new Iterator<>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < values.length;
			}

			@Override
			public GeneratedRecipe next() {
				if (!hasNext()) throw new NoSuchElementException();
				return values[index++];
			}
		};
	}

	public static class CRNullableDyedRecipeList extends CRDyedRecipeList {

		public CRNullableDyedRecipeList(
			Function<@Nullable DyeColor, GeneratedRecipe> filler
		) {
			super(filler);
			values[values.length - 1] = filler.apply(null);
		}

		@Override
		protected int getColorCount() {
			return COLOR_AMOUNT + 1;
		}

		@Override
		public GeneratedRecipe get(@Nullable DyeColor color) {
			if (color == null) return values[values.length - 1];
			return super.get(color);
		}
	}
}
