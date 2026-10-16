package com.yanny.ali.emi.compatibility.emi;

import com.yanny.ali.mixin.MixinVegetationBlock;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EmiBlockSlotWidget extends SlotWidget {
    private final ClientLevel level;
    private final BlockState blockState;
    private final Block block;
    private final boolean isPlant;

    public EmiBlockSlotWidget(Block block, int x, int y) {
        super(EmiStack.of(block), x, y);
        this.block = block;
        blockState = block.defaultBlockState();
        isPlant = block instanceof VegetationBlock;
        level = Minecraft.getInstance().level;
        large(true);
    }

    // Fleet port (26.1): blocks are drawn as GUI picture-in-picture render states (same as ALI's JEI widget).
    private static final float BLOCK_SIZE = 18;
    private static final float PLANT_SIZE = 9;

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        drawBackground(guiGraphics, mouseX, mouseY, delta);
        drawOverlay(guiGraphics, mouseX, mouseY, delta);
        org.joml.Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        int px = (int) pose.m20();
        int py = (int) pose.m21();
        pose.popMatrix();
        net.minecraft.client.gui.navigation.ScreenRectangle viewport = com.yanny.aci.compatibility.AbstractScrollWidget.getCurrentViewport();
        int cx = px + 13;
        int cy = py + 13;
        if (isPlant) {
            BlockState base;
            BlockState farmland = Blocks.FARMLAND.defaultBlockState();
            if (block instanceof MixinVegetationBlock vegetationBlock && vegetationBlock.invokeAliMayPlaceOn(farmland, level, BlockPos.ZERO)) {
                base = farmland;
            } else {
                base = Blocks.GRASS_BLOCK.defaultBlockState();
            }
            com.yanny.ali.platform.Services.getClientPlatform().renderBlockInGui(guiGraphics,
                com.yanny.ali.pip.BlockRenderState.centered(base, level, cx, cy + 4, PLANT_SIZE, viewport));
            com.yanny.ali.platform.Services.getClientPlatform().renderBlockInGui(guiGraphics,
                com.yanny.ali.pip.BlockRenderState.centered(blockState, level, cx, cy - 4, PLANT_SIZE, viewport));
        } else {
            com.yanny.ali.platform.Services.getClientPlatform().renderBlockInGui(guiGraphics,
                com.yanny.ali.pip.BlockRenderState.centered(blockState, level, cx, cy, BLOCK_SIZE, viewport));
        }
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (block.asItem() == Items.AIR) {
            return List.of(ClientTooltipComponent.create(Component.translatable(block.getDescriptionId()).getVisualOrderText()));
        }

        return super.getTooltip(mouseX, mouseY);
    }
}
