package dev.mayaqq.estrogen.neoforge.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mayaqq.estrogen.content.EstrogenTags;
import dev.mayaqq.estrogen.content.recipes.SpongingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {

    @WrapOperation(
            method = "removeWaterBreadthFirstSearch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;breadthFirstTraversal(Lnet/minecraft/core/BlockPos;IILjava/util/function/BiConsumer;Ljava/util/function/Predicate;)I"
            )
    )
    private int wrapSpongeTraversal(
            BlockPos startPos,
            int maxDepth,
            int maxNodes,
            BiConsumer<BlockPos, Consumer<BlockPos>> consumer,
            Predicate<BlockPos> originalPredicate,
            Operation<Integer> original,
            Level level,
            BlockPos center
    ) {
        Predicate<BlockPos> wrappedPredicate = pos -> {
            BlockState blockState = level.getBlockState(pos);
            FluidState fluidState = level.getFluidState(pos);

            if (fluidState.is(EstrogenTags.Fluids.INSTANCE.getSPONGE_IGNORING())) {
                return false;
            }

            BlockState suckedUp = SpongingRecipe.onSuckUp(blockState, fluidState, center, level, pos);
            if (suckedUp != null) {
                level.setBlock(pos, suckedUp, 3);
                return true;
            }

            return originalPredicate.test(pos);
        };

        return original.call(startPos, maxDepth, maxNodes, consumer, wrappedPredicate);
    }
}