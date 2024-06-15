package qzimyion.betterturtlehelmets.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class TurtleShellMixin extends LivingEntity {


    protected TurtleShellMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean isCreative();

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Inject(method = "updateTurtleHelmet", at = @At("HEAD"))
    private void updateTurtleHelmet(CallbackInfo ci){
        ItemStack itemStack = getEquippedStack(EquipmentSlot.HEAD);
        if (!isCreative() && canMoveVoluntarily() && !itemStack.isEmpty() && itemStack.isOf(Items.TURTLE_HELMET)){
            int damageTime = EnchantmentHelper.getLevel(Enchantments.RESPIRATION, itemStack) > 0 ? 340 * (1 + EnchantmentHelper.getLevel(Enchantments.RESPIRATION, itemStack) / 2) : 340;
            if (isSubmergedIn(FluidTags.WATER)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 100));
                if (getWorld().getTime() % damageTime == 0){
                    itemStack.damage(1, this, EquipmentSlot.HEAD);
                }
            }
        }
    }

}
