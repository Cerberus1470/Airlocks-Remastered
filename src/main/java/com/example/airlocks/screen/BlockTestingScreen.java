package com.example.airlocks.screen;

import com.example.airlocks.Airlocks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BlockTestingScreen extends AbstractContainerScreen<BlockTestingMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Airlocks.MODID, "textures/gui/gem_polishing_station_gui.png");

    public BlockTestingScreen(BlockTestingMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) /2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x+85, y+30, 176, 0, 7, menu.getScaledProgress());
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, x, y, delta);
        renderTooltip(guiGraphics, x, y);
    }
}
