package me.donut.gui;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final List<Panel> panels = new ArrayList<>();

    public ClickGuiScreen() {
        super(Text.literal("Donut Client"));
    }

    @Override
    protected void init() {
        if (panels.isEmpty()) {
            int x = 20;
            for (Category c : Category.values()) {
                List<Module> mods = ModuleManager.getByCategory(c);
                int height = Math.max(220, mods.size() * 20 + 30);
                panels.add(new Panel(c, x, 25, 140, height));
                x += 150;
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        for (Panel p : panels) {
            p.render(ctx, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (Panel p : panels) {
            if (p.mouseClicked(click.x(), click.y(), click.buttonInfo().button())) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        for (Panel p : panels) {
            p.mouseReleased();
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        client.setScreen(null);
    }

    private class Panel {
        private static final int HEADER_H = 22;
        private static final int ROW_H = 20;
        private static final int PADDING = 6;

        Category category;
        int x, y, w, h;
        boolean dragging = false;
        int dragOffX, dragOffY;

        Panel(Category category, int x, int y, int w, int h) {
            this.category = category;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void render(DrawContext ctx, int mouseX, int mouseY) {
            ctx.fill(x, y, x + w, y + h, 0xC0101010);
            ctx.fill(x, y, x + w, y + HEADER_H, 0xE0252525);
            // border
            ctx.fill(x, y, x + w, y + 1, 0x33FFFFFF);
            ctx.fill(x, y + h - 1, x + w, y + h, 0x33FFFFFF);
            ctx.fill(x, y, x + 1, y + h, 0x33FFFFFF);
            ctx.fill(x + w - 1, y, x + w, y + h, 0x33FFFFFF);

            String title = category.displayName.toUpperCase();
            ctx.drawTextWithShadow(textRenderer, title, x + PADDING + 2, y + 7, 0xFFFFFF);

            List<Module> mods = ModuleManager.getByCategory(category);
            int rowY = y + HEADER_H;
            for (Module m : mods) {
                if (rowY + ROW_H > y + h) break;
                ctx.fill(x + 1, rowY, x + w - 1, rowY + ROW_H,
                        m.isEnabled() ? 0xFF7B2FBE : 0x00000000);
                ctx.drawTextWithShadow(textRenderer, m.getName().toUpperCase(),
                        x + PADDING, rowY + 6, 0xC8C8C8);
                rowY += ROW_H;
            }
        }

        boolean inHeader(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + HEADER_H;
        }

        boolean inRow(double mx, double my) {
            return mx >= x && mx <= x + w && my > y + HEADER_H && my <= y + h;
        }

        int rowAt(double my) {
            if (my <= y + HEADER_H || my > y + h) return -1;
            int i = (int) ((my - y - HEADER_H) / ROW_H);
            List<Module> mods = ModuleManager.getByCategory(category);
            return (i >= 0 && i < mods.size()) ? i : -1;
        }

        boolean mouseClicked(double mx, double my, int button) {
            if (mx < x || mx > x + w) return false;
            if (my < y || my > y + h) return false;

            if (inHeader(mx, my)) {
                if (button == 0) {
                    dragging = true;
                    dragOffX = (int) (mx - x);
                    dragOffY = (int) (my - y);
                }
                return true;
            }

            int row = rowAt(my);
            if (row >= 0 && button == 0) {
                Module m = ModuleManager.getByCategory(category).get(row);
                m.toggle();
                return true;
            }
            return true;
        }

        void mouseReleased() {
            dragging = false;
        }
    }
}
