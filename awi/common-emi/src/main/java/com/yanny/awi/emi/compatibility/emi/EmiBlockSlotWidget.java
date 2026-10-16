package com.yanny.awi.emi.compatibility.emi;

import com.yanny.aci.compatibility.ScrollableTooltip;
import com.yanny.aci.tooltip.CoreTooltipUtils;
import com.yanny.aci.tooltip.TooltipLine;
import com.yanny.awi.api.IDataNode;
import com.yanny.awi.plugin.client.TooltipUtils;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EmiBlockSlotWidget extends SlotWidget {
    private final BlockState blockState;
    private final IDataNode entry;

    public EmiBlockSlotWidget(IDataNode entry, Block block, int x, int y) {
        super(EmiStack.of(block), x, y);
        this.blockState = block.defaultBlockState();
        this.entry = entry;
    }

    @Override
    protected void addSlotTooltip(List<ClientTooltipComponent> list) {
        List<TooltipLine> lines = CoreTooltipUtils.toLines(entry.getTooltip(), 0, Minecraft.getInstance().options.advancedItemTooltips, TooltipUtils.getStyle(), TooltipUtils.getNumberOptions());

        if (!lines.isEmpty()) {
            list.add(new ScrollableTooltip(lines));
        }

        super.addSlotTooltip(list);
    }

    // Fleet port (26.1): blocks are drawn as GUI picture-in-picture render states (same as AWI's JEI widget).
    private static final float BLOCK_SIZE = 9;

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        drawBackground(guiGraphics, mouseX, mouseY, delta);
        drawOverlay(guiGraphics, mouseX, mouseY, delta);
        org.joml.Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        int cx = (int) pose.m20() + 9;
        int cy = (int) pose.m21() + 9;
        pose.popMatrix();
        com.yanny.awi.platform.Services.getClientPlatform().renderBlockInGui(guiGraphics,
            com.yanny.awi.pip.BlockRenderState.centered(blockState, net.minecraft.client.Minecraft.getInstance().level, cx, cy,
                BLOCK_SIZE, com.yanny.aci.compatibility.AbstractScrollWidget.getCurrentViewport()));
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (blockState.getBlock().asItem() == Items.AIR) {
            return List.of(ClientTooltipComponent.create(Component.translatable(blockState.getBlock().getDescriptionId()).getVisualOrderText()));
        }

        return super.getTooltip(mouseX, mouseY);
    }
}
