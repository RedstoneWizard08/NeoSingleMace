package redstonedev.neosinglemace.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redstonedev.neosinglemace.NeoSingleMace;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Shadow @Final ItemStack result;

    @Inject(method = "matches*", at = @At("RETURN"), cancellable = true)
    public void overrideMatches(CallbackInfoReturnable<Boolean> cir) {
        if (NeoSingleMace.isMaceCrafted() && this.result.is(Items.MACE)) cir.setReturnValue(false);
    }
}
