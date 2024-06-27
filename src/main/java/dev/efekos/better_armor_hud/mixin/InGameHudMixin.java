package dev.efekos.better_armor_hud.mixin;

import dev.efekos.better_armor_hud.client.BetterArmorHUDClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int renderHealthValue;
    @Shadow
    @Final
    private static Identifier ARMOR_EMPTY_TEXTURE;
    @Unique
    private static final Identifier DIAMOND_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/diamond_full");
    @Unique
    private static final Identifier DIAMOND_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/diamond_half_right");
    @Unique
    private static final Identifier DIAMOND_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/diamond_half_left");
    @Unique
    private static final Identifier NETHERITE_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/netherite_full");
    @Unique
    private static final Identifier NETHERITE_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/netherite_half_left");
    @Unique
    private static final Identifier NETHERITE_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/netherite_half_right");
    @Unique
    private static final Identifier GOLD_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/gold_full");
    @Unique
    private static final Identifier GOLD_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/gold_half_right");
    @Unique
    private static final Identifier GOLD_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/gold_half_left");
    @Unique
    private static final Identifier IRON_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/iron_full");
    @Unique
    private static final Identifier IRON_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/iron_half_right");
    @Unique
    private static final Identifier IRON_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/iron_half_left");
    @Unique
    private static final Identifier LEATHER_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/leather_full");
    @Unique
    private static final Identifier LEATHER_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/leather_half_right");
    @Unique
    private static final Identifier LEATHER_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/leather_half_left");
    @Unique
    private static final Identifier CHAIN_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/chainmail_full");
    @Unique
    private static final Identifier CHAIN_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/chainmail_half_right");
    @Unique
    private static final Identifier CHAIN_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/chainmail_half_left");
    @Unique
    private static final Identifier TURTLE_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/turtle_full");
    @Unique
    private static final Identifier TURTLE_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/turtle_half_right");
    @Unique
    private static final Identifier TURTLE_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/turtle_half_left");
    @Unique
    private static final Identifier TOUGHNESS_FULL = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/toughness_full");
    @Unique
    private static final Identifier TOUGHNESS_HALF = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/toughness_half");
    @Unique
    private static final Identifier UNKNOWN_HALF_LEFT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/unknown_half_left");
    @Unique
    private static final Identifier UNKNOWN_HALF_RIGHT_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/unknown_half_right");
    @Unique
    private static final Identifier UNKNOWN_FULL_TEXTURE = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/unknown_full");
    @Unique
    private static final Identifier KNOCKBACK_RESISTANCE_FULL = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/knockback_resistance_full");
    @Unique
    private static final Identifier KNOCKBACK_RESISTANCE_HALF = Identifier.of(BetterArmorHUDClient.MOD_ID, "hud/knockback_resistance_half");

    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    public void renderStatusBars(DrawContext context, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null && playerEntity.getArmor() > 0) {
            int height = context.getScaledWindowHeight();
            int width = context.getScaledWindowWidth();

            int x = width / 2 - 91;

            // all I know about this is it finds the correct y value.
            int ii = MathHelper.ceil(playerEntity.getHealth());
            int j = this.renderHealthValue;
            int o = height - 39;
            float f = Math.max((float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float) Math.max(j, ii));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int y = o - (q - 1) * r - 10;

            List<ItemStack> armorStacks = new ArrayList<>();
            if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY)
                armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.CHEST));
            if (playerEntity.getEquippedStack(EquipmentSlot.LEGS) != ItemStack.EMPTY)
                armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.LEGS));
            if (playerEntity.getEquippedStack(EquipmentSlot.HEAD) != ItemStack.EMPTY)
                armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.HEAD));
            if (playerEntity.getEquippedStack(EquipmentSlot.FEET) != ItemStack.EMPTY)
                armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.FEET));

            int toughness = (int) calculateToughness(armorStacks);
            int knockbackResistance = (int) calculateKnockbackResistance(armorStacks);
            AtomicInteger leatherLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.LEATHER.value(), armorStacks));
            AtomicInteger chainLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.CHAIN.value(), armorStacks));
            AtomicInteger netheriteLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.NETHERITE.value(), armorStacks));
            AtomicInteger turtleLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.TURTLE.value(), armorStacks));
            AtomicInteger diamondLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.DIAMOND.value(), armorStacks));
            AtomicInteger goldLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.GOLD.value(), armorStacks));
            AtomicInteger ironLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.IRON.value(), armorStacks));
            AtomicInteger otherLevel = new AtomicInteger(
                    playerEntity.getArmor() -
                            leatherLevel.get() -
                            chainLevel.get() -
                            netheriteLevel.get() -
                            turtleLevel.get() -
                            diamondLevel.get() -
                            goldLevel.get() -
                            ironLevel.get()
            );

            //armor
            for (int i = 0; i < 10; i++) {
                context.drawGuiTexture(ARMOR_EMPTY_TEXTURE, x + i * 8, y, 9, 9);

                if (netheriteLevel.get() >= 2) {
                    context.drawGuiTexture(NETHERITE_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    netheriteLevel.addAndGet(-2);
                } else if (netheriteLevel.get() == 1) {
                    context.drawGuiTexture(NETHERITE_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    netheriteLevel.addAndGet(-1);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);

                } else if (diamondLevel.get() >= 2) {
                    diamondLevel.addAndGet(-2);
                    context.drawGuiTexture(DIAMOND_FULL_TEXTURE, x + i * 8, y, 9, 9);

                } else if (diamondLevel.get() == 1) {

                    context.drawGuiTexture(DIAMOND_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    diamondLevel.addAndGet(-1);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);

                } else if (goldLevel.get() >= 2) {

                    context.drawGuiTexture(GOLD_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    goldLevel.addAndGet(-2);
                } else if (goldLevel.get() == 1) {
                    context.drawGuiTexture(GOLD_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    goldLevel.addAndGet(-1);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);

                } else if (ironLevel.get() >= 2) {
                    context.drawGuiTexture(IRON_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    ironLevel.addAndGet(-2);
                } else if (ironLevel.get() == 1) {
                    context.drawGuiTexture(IRON_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    ironLevel.getAndDecrement();

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);

                } else if (leatherLevel.get() >= 2) {
                    context.drawGuiTexture(LEATHER_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    leatherLevel.addAndGet(-2);
                } else if (leatherLevel.get() == 1) {

                    context.drawGuiTexture(LEATHER_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    leatherLevel.addAndGet(-1);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);

                } else if (chainLevel.get() >= 2) {
                    context.drawGuiTexture(CHAIN_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    chainLevel.addAndGet(-2);
                } else if (chainLevel.get() == 1) {
                    context.drawGuiTexture(CHAIN_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);
                    chainLevel.getAndDecrement();

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);
                } else if (turtleLevel.get() >= 2) {

                    context.drawGuiTexture(TURTLE_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    turtleLevel.addAndGet(-2);

                } else if (turtleLevel.get() == 1) {
                    turtleLevel.getAndDecrement();
                    context.drawGuiTexture(TURTLE_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);
                } else if (otherLevel.get() >= 2) {
                    context.drawGuiTexture(UNKNOWN_FULL_TEXTURE, x + i * 8, y, 9, 9);
                    otherLevel.getAndAdd(-2);
                } else if (otherLevel.get() == 1) {
                    otherLevel.getAndDecrement();
                    context.drawGuiTexture(UNKNOWN_HALF_LEFT_TEXTURE, x + i * 8, y, 9, 9);

                    checkForRights(context, x, y, i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel, otherLevel);
                }

            }

            //toughness
            for (int i = 0; i < 10; i++) {
                if (toughness >= 2) {
                    context.drawGuiTexture(TOUGHNESS_FULL, x + i * 8, y, 9, 9);
                    toughness -= 2;
                } else if (toughness == 1) {
                    context.drawGuiTexture(TOUGHNESS_HALF, x + i * 8, y, 9, 9);
                    toughness--;
                }
            }


            //knockback resistance
            for (int i = 0; i < 10; i++) {
                if (knockbackResistance >= 2) {
                    context.drawGuiTexture(KNOCKBACK_RESISTANCE_FULL, x + i * 8, y, 9, 9);
                    knockbackResistance -= 2;
                } else if (knockbackResistance == 1) {
                    context.drawGuiTexture(KNOCKBACK_RESISTANCE_HALF, x + i * 8, y, 9, 9);
                    knockbackResistance--;
                }
            }
        }
    }

    @Unique
    private int calculateArmorFor(ArmorMaterial material, List<ItemStack> items) {
        int i = 0;
        for (ItemStack item : items) {
            if ((item.getItem() instanceof ArmorItem armorItem) && armorItem.getMaterial().equals(material))
                i += armorItem.getProtection();
        }
        return i;
    }

    @Unique
    private float calculateToughness(List<ItemStack> items) {
        int i = 0;
        for (ItemStack item : items) {
            if ((item.getItem() instanceof ArmorItem armorItem)) i += (int) (armorItem.getMaterial().value().toughness());
        }
        return i;
    }

    @Unique
    private float calculateKnockbackResistance(List<ItemStack> items) {
        int i = 0;
        for (ItemStack item : items) {
            if ((item.getItem() instanceof ArmorItem armorItem))
                i += (int) (armorItem.getMaterial().value().knockbackResistance() * 10.0F);
        }
        return i;
    }

    @Unique
    private void checkForRights(DrawContext context, int x, int y, int i, AtomicInteger netheriteLevel, AtomicInteger diamondLevel, AtomicInteger goldLevel, AtomicInteger ironLevel, AtomicInteger leatherLevel, AtomicInteger chainLevel, AtomicInteger turtleLevel, AtomicInteger otherLevel) {
        if (netheriteLevel.get() >= 1) {
            context.drawGuiTexture(NETHERITE_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            netheriteLevel.getAndDecrement();

        } else if (diamondLevel.get() >= 1) {
            context.drawGuiTexture(DIAMOND_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            diamondLevel.getAndDecrement();

        } else if (goldLevel.get() >= 1) {
            context.drawGuiTexture(GOLD_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            goldLevel.getAndDecrement();

        } else if (ironLevel.get() >= 1) {
            context.drawGuiTexture(IRON_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            ironLevel.getAndDecrement();

        } else if (leatherLevel.get() >= 1) {
            context.drawGuiTexture(LEATHER_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            leatherLevel.getAndDecrement();

        } else if (chainLevel.get() >= 1) {
            context.drawGuiTexture(CHAIN_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            chainLevel.getAndDecrement();

        } else if (turtleLevel.get() >= 1) {
            context.drawGuiTexture(TURTLE_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            turtleLevel.getAndDecrement();
        } else if (otherLevel.get() >= 1) {
            context.drawGuiTexture(UNKNOWN_HALF_RIGHT_TEXTURE, x + i * 8, y, 9, 9);
            otherLevel.getAndDecrement();
        }
    }
}
