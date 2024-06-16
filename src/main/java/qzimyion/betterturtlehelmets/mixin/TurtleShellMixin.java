package qzimyion.betterturtlehelmets.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Mixin(PlayerEntity.class)
public abstract class TurtleShellMixin extends LivingEntity {


    protected TurtleShellMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean isCreative();

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Unique
    RegistryEntry<Enchantment> entry = getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
            .getEntry(Enchantments.RESPIRATION).get();

    @Inject(method = "updateTurtleHelmet", at = @At("HEAD"))
    private void updateTurtleHelmet(CallbackInfo ci){
        ItemStack itemStack = getEquippedStack(EquipmentSlot.HEAD);
        if (!isCreative() && canMoveVoluntarily() && !itemStack.isEmpty() && itemStack.isOf(Items.TURTLE_HELMET)){
            int damageTimeHelmet = switch (EnchantmentHelper.getLevel(entry, itemStack)) {
                case 1 -> 600;
                case 2 -> 1400;
                case 3 -> 3200;
                case 4 -> 5600; //In case quark or some other mod that adds higher level chant is loaded on
                default -> 300;
            };
            if (isSubmergedIn(FluidTags.WATER)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 100));
                if (getWorld().getTime() % damageTimeHelmet == 0){
                    itemStack.damage(1, this, EquipmentSlot.HEAD);
                }
            }
        }
    }

}
