package com.BAVDE.atium_mod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.BAVDE.atium_mod.AtiumMod;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public class InfusingTableScreen extends AbstractContainerScreen<InfusingTableMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AtiumMod.MOD_ID, "textures/gui/infusing_table_gui.png");

    public InfusingTableScreen(InfusingTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    //renders the inventory texture
    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTicks, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int imageX = 176;
        int imageY = 220;

        int x = (this.width - imageX) / 2;
        int y = (this.height - imageY) / 2;

        //is displayed
        this.blit(pPoseStack, x, y, 0, 0, imageX, imageY);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}