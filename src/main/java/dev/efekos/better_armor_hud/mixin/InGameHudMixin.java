package dev.efekos.better_armor_hud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.efekos.better_armor_hud.client.BetterArmorHUDClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow private int renderHealthValue;
    @Unique
    private static final Identifier ICONS = new Identifier(BetterArmorHUDClient.MOD_ID,"textures/gui/armor_icons.png");
    @Unique
    private static final Identifier VANILLA_ICONS = new Identifier("textures/gui/icons.png");

    @Inject(method = "renderStatusBars",at = @At("TAIL"))
    public void renderStatusBars(MatrixStack matricies, CallbackInfo ci){
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null&&playerEntity.getArmor()>0) {

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1,1,1,1);
            RenderSystem.setShaderTexture(0,ICONS);

            int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
            int width = MinecraftClient.getInstance().getWindow().getScaledWidth();

            int x = width/2-91;

            // all I know about this is it finds the correct y value.
            int ii = MathHelper.ceil(playerEntity.getHealth());
            int j = this.renderHealthValue;
            int o = height - 39;
            float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, ii));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int y = o - (q - 1) * r - 10;

            List<ItemStack> armorStacks = new ArrayList<>();
            if(playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY) armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.CHEST));
            if(playerEntity.getEquippedStack(EquipmentSlot.LEGS) != ItemStack.EMPTY) armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.LEGS));
            if(playerEntity.getEquippedStack(EquipmentSlot.HEAD) != ItemStack.EMPTY) armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.HEAD));
            if(playerEntity.getEquippedStack(EquipmentSlot.FEET) != ItemStack.EMPTY) armorStacks.add(playerEntity.getEquippedStack(EquipmentSlot.FEET));

            int toughness = (int) calculateToughness(armorStacks);
            int knockbackResistance = (int) calculateKnockbackResistance(armorStacks);
            AtomicInteger leatherLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.LEATHER, armorStacks));
            AtomicInteger chainLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.CHAIN, armorStacks));
            AtomicInteger netheriteLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.NETHERITE, armorStacks));
            AtomicInteger turtleLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.TURTLE, armorStacks));
            AtomicInteger diamondLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.DIAMOND, armorStacks));
            AtomicInteger goldLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.GOLD, armorStacks));
            AtomicInteger ironLevel = new AtomicInteger(calculateArmorFor(ArmorMaterials.IRON, armorStacks));
            AtomicInteger otherLevel = new AtomicInteger(
                    playerEntity.getArmor()-
                            leatherLevel.get()-
                            chainLevel.get()-
                            netheriteLevel.get()-
                            turtleLevel.get()-
                            diamondLevel.get()-
                            goldLevel.get()-
                            ironLevel.get()
            );

            //armor
            for (int i = 0; i < 10; i++) {
                drawTexture(matricies,x+i*8,y,0,0,9,9);

                if(netheriteLevel.get() >=2) {
                    drawTexture(matricies,x+i*8,y,0,54,9,9);
                    netheriteLevel.addAndGet(-2);
                }
                else if (netheriteLevel.get() ==1) {
                    drawTexture(matricies,x+i*8,y,9,54,9,9);
                    netheriteLevel.addAndGet(-1);

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);

                } else if (diamondLevel.get() >=2){
                    diamondLevel.addAndGet(-2);
                    drawTexture(matricies,x+i*8,y,0,18,9,9);

                } else if (diamondLevel.get() ==1){

                    drawTexture(matricies,x+i*8,y,9,18,9,9);
                    diamondLevel.addAndGet(-1);

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);

                } else if(goldLevel.get() >=2){

                    drawTexture(matricies,x+i*8,y,0,27,9,9);
                    goldLevel.addAndGet(-2);
                } else if (goldLevel.get() ==1){
                    drawTexture(matricies,x+i*8,y,9,27,9,9);
                    goldLevel.addAndGet(-1);

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);

                } else if (ironLevel.get() >=2 ) {
                    drawTexture(matricies,x+i*8,y,0,9,9,9);
                    ironLevel.addAndGet(-2);
                } else if (ironLevel.get() ==1){
                    drawTexture(matricies,x+i*8,y,9,9,9,9);
                    ironLevel.getAndDecrement();

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);

                } else if (leatherLevel.get() >= 2){
                    drawTexture(matricies,x+i*8,y,0,36,9,9);
                    leatherLevel.addAndGet(-2);
                } else if (leatherLevel.get() == 1){

                    drawTexture(matricies,x+i*8,y,9,36,9,9);
                    leatherLevel.addAndGet(-1);

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);

                } else if (chainLevel.get() >=2){
                    drawTexture(matricies,x+i*8,y,0,45,9,9);
                    chainLevel.addAndGet(-2);
                } else if (chainLevel.get() ==1){
                    drawTexture(matricies,x+i*8,y,9,45,9,9);
                    chainLevel.getAndDecrement();

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);
                } else if (turtleLevel.get() >=2){

                    drawTexture(matricies,x+i*8,y,0,63,9,9);
                    turtleLevel.addAndGet(-2);

                } else if (turtleLevel.get() == 1){
                    turtleLevel.getAndDecrement();
                    drawTexture(matricies,x+i*8,y,9,63,9,9);

                    checkForRights(matricies,x,y,i, netheriteLevel, diamondLevel, goldLevel, ironLevel, leatherLevel, chainLevel, turtleLevel,otherLevel);
                } else if (otherLevel.get()>=2){
                    drawTexture(matricies,x+i*8,y,0,72,9,9);
                    otherLevel.getAndAdd(-2);
                } else if (otherLevel.get()==1){
                    otherLevel.getAndDecrement();
                    drawTexture(matricies,x+i*8,y,9,72,9,9);

                    checkForRights(matricies,x,y,i,netheriteLevel,diamondLevel,goldLevel,ironLevel,leatherLevel,chainLevel,turtleLevel,otherLevel);
                }

            }

            //toughness
            for (int i = 0; i < 10; i++) {
                if(toughness>=2){
                    drawTexture(matricies,x+i*8,y,0,90,9,9);
                    toughness-=2;
                } else if(toughness==1){
                    drawTexture(matricies,x+i*8,y,9,90,9,9);
                    toughness--;
                }
            }


            //knockback resistance
            for (int i = 0; i < 10; i++) {
                if(knockbackResistance>=2){
                    drawTexture(matricies,x+i*8,y,0,81,9,9);
                    knockbackResistance-=2;
                } else if(knockbackResistance==1){
                    drawTexture(matricies,x+i*8,y,9,81,9,9);
                    knockbackResistance--;
                }
            }
        }
        RenderSystem.setShaderTexture(0,VANILLA_ICONS);
    }

    @Unique
    private int calculateArmorFor(ArmorMaterial material, List<ItemStack> items){
        int i = 0;
        for (ItemStack item : items) {
            if((item.getItem() instanceof ArmorItem armorItem) && armorItem.getMaterial().equals(material)) i += armorItem.getProtection();
        }
        return i;
    }

    @Unique
    private float calculateToughness(List<ItemStack> items){
        int i = 0;
        for (ItemStack item : items) {
            if((item.getItem() instanceof ArmorItem armorItem)) i += (int) (armorItem.getMaterial().getToughness());
        }
        return i;
    }

    @Unique
    private float calculateKnockbackResistance(List<ItemStack> items){
        int i = 0;
        for (ItemStack item : items) {
            if((item.getItem() instanceof ArmorItem armorItem)) i += (int) (armorItem.getMaterial().getKnockbackResistance()*10.0F);
        }
        return i;
    }

    @Unique
    private void checkForRights(MatrixStack matricies,int x,int y,int i,AtomicInteger netheriteLevel, AtomicInteger diamondLevel, AtomicInteger goldLevel, AtomicInteger ironLevel, AtomicInteger leatherLevel, AtomicInteger chainLevel, AtomicInteger turtleLevel,AtomicInteger otherLevel) {
        if(netheriteLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,54,9,9);
            netheriteLevel.getAndDecrement();

        } else if (diamondLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,18,9,9);
            diamondLevel.getAndDecrement();

        } else if (goldLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18 ,27,9,9);
            goldLevel.getAndDecrement();

        } else if (ironLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,9,9,9);
            ironLevel.getAndDecrement();

        } else if (leatherLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,36,9,9);
            leatherLevel.getAndDecrement();

        } else if (chainLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18 ,45,9,9);
            chainLevel.getAndDecrement();

        } else if (turtleLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,63,9,9);
            turtleLevel.getAndDecrement();
        } else if(otherLevel.get()>=1){
            drawTexture(matricies,x+i*8,y,18,72,9,9);
            otherLevel.getAndDecrement();
        }
    }
}
