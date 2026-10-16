package com.yanny.awi.emi.compatibility.emi;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import com.yanny.aci.api.NumberExpr;
import com.yanny.aci.api.NumberInterval;
import com.yanny.aci.number.NumberFormatter;
import com.yanny.aci.compatibility.ScrollableTooltip;
import com.yanny.aci.tooltip.CoreTooltipUtils;
import com.yanny.aci.tooltip.TooltipLine;
import com.yanny.awi.api.IDataNode;
import com.yanny.awi.plugin.client.TooltipUtils;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiLootSlotWidget extends SlotWidget {
    private final IDataNode entry;
    @Nullable
    private Component count;
    private boolean isRange = false;

    public EmiLootSlotWidget(IDataNode entry, EmiIngredient ingredient, int x, int y, NumberExpr count) {
        super(ingredient, x, y);
        this.entry = entry;
        setCount(count);
    }

    @Override
    protected void addSlotTooltip(List<ClientTooltipComponent> list) {
        List<TooltipLine> lines = CoreTooltipUtils.toLines(entry.getTooltip(), 0, Minecraft.getInstance().options.advancedItemTooltips, TooltipUtils.getStyle(), TooltipUtils.getNumberOptions());

        if (!lines.isEmpty()) {
            list.add(new ScrollableTooltip(lines));
        }

        super.addSlotTooltip(list);
    }

    @Override
    public void drawOverlay(GuiGraphicsExtractor draw, int mouseX, int mouseY, float delta) {
        if (count != null) {
            Font font = Minecraft.getInstance().font;
            org.joml.Matrix3x2fStack stack = draw.pose();

            stack.pushMatrix();

            if (isRange) {
                stack.translate(x + 17, y + 13);
                stack.pushMatrix();
                stack.scale(0.5f, 0.5f);
                //draw.fill(-font.width(count) - 2, -2, 2, 10, 255<<24 | 0);
                draw.text(font, count, -font.width(count), 0, 16777215, false);
                stack.popMatrix();
            } else {
                stack.translate(x + 18, y + 10);
                draw.text(font, count, -font.width(count), 0, 16777215, true);
            }

            stack.popMatrix();
        }

        super.drawOverlay(draw, mouseX, mouseY, delta);
    }

    private void setCount(NumberExpr count) {
        NumberInterval bounds = NumberFormatter.slotBounds(count);

        if (!bounds.isPoint() || bounds.lo() > 1) {
            this.count = Component.literal(NumberFormatter.slot(bounds));
            isRange = !bounds.isPoint();
        }
    }
}
