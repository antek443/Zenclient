package com.grimclient.gui;

import com.grimclient.GrimClientMod;
import com.grimclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class GrimGUI extends Screen {

    private static final int PW = 780, PH = 520;
    private static final int SW = 162;
    private static final int HH = 44;

    private static final int C_BG      = 0xFF111114;
    private static final int C_SB      = 0xFF0D0D10;
    private static final int C_HDR     = 0xFF09090C;
    private static final int C_CARD    = 0xFF161619;
    private static final int C_CARD_H  = 0xFF1C1C22;
    private static final int C_CARD_ON = 0xFF17172A;
    private static final int C_ACC     = 0xFF7B6FFF;
    private static final int C_ACC_DIM = 0xFF3D3880;
    private static final int C_WHITE   = 0xFFEEEEFF;
    private static final int C_GRAY    = 0xFF8888AA;
    private static final int C_DGRAY   = 0xFF3A3A50;
    private static final int C_SEP     = 0xFF1A1A22;
    private static final int C_SRCH    = 0xFF141418;

    // Keybind popup
    private static final int C_POPUP_BG     = 0xFF0E0E18;
    private static final int C_POPUP_BORDER = 0xFF7B6FFF;

    private int px, py;
    private Module.Category selCat = Module.Category.COMBAT;
    private int scroll = 0, maxScroll = 0;
    private boolean drag = false;
    private int dragX, dragY;

    private String searchQuery = "";
    private boolean searchMode = false;
    private boolean searchFocused = false;

    // Keybind system
    private Module bindingModule = null;  // moduł który aktualnie bindujemy
    private boolean waitingForKey = false;

    public GrimGUI() {
        super(Text.literal("Grim Client"));
    }

    @Override
    protected void init() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
        scroll = 0;
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext c, int mx, int my, float dt) {
        c.fill(0, 0, width, height, 0xBB000000);

        // Panel
        c.fill(px, py, px+PW, py+PH, C_BG);
        c.fill(px, py, px+SW, py+PH, C_SB);
        c.fill(px+SW, py, px+SW+1, py+PH, C_SEP);
        c.fill(px, py, px+PW, py+HH, C_HDR);
        c.fill(px, py+HH, px+PW, py+HH+1, C_SEP);
        // Accent top bar
        c.fill(px, py, px+PW, py+2, C_ACC);

        // Logo
        drawSkull(c, px+10, py+8);
        c.drawText(textRenderer, "Grim Client", px+36, py+9, C_WHITE, false);
        c.drawText(textRenderer, "Version: 1.0", px+37, py+21, C_DGRAY, false);

        // Search box
        int sbx = px+SW+10, sby = py+(HH-18)/2;
        c.fill(sbx, sby, sbx+190, sby+18, C_SRCH);
        c.fill(sbx, sby+17, sbx+190, sby+18, searchFocused ? C_ACC : C_DGRAY);
        String sd = searchQuery.isEmpty() && !searchFocused ? "Search modules..." : searchQuery+(searchFocused?"|":"");
        c.drawText(textRenderer, sd, sbx+6, sby+5, searchQuery.isEmpty() ? C_DGRAY : C_WHITE, false);

        // Active count
        int en = GrimClientMod.core.moduleManager.getEnabled().size();
        String enStr = en+" active";
        c.drawText(textRenderer, enStr, px+PW-textRenderer.getWidth(enStr)-10, py+HH-13, C_ACC, false);
        c.drawText(textRenderer, "[P]", px+PW-22, py+8, C_DGRAY, false);

        // Sidebar categories
        c.drawText(textRenderer, "FEATURES", px+10, py+HH+8, C_DGRAY, false);
        int ty = py+HH+22;
        for (Module.Category cat : new Module.Category[]{
            Module.Category.COMBAT, Module.Category.MOVEMENT,
            Module.Category.PLAYER, Module.Category.VISUALS
        }) {
            boolean sel = !searchMode && cat == selCat;
            boolean hov = in(mx,my, px, ty, SW, 26);
            if (sel) { c.fill(px, ty, px+SW, ty+26, 0xFF18182A); c.fill(px, ty, px+3, ty+26, C_ACC); }
            else if (hov) { c.fill(px, ty, px+SW, ty+26, 0xFF131318); }
            c.drawText(textRenderer, cat.icon+" "+cat.label, px+12, ty+8, sel ? C_WHITE : C_GRAY, false);
            long enCat = GrimClientMod.core.moduleManager.getByCategory(cat).stream().filter(Module::isEnabled).count();
            if (enCat > 0) {
                c.fill(px+SW-22, ty+6, px+SW-4, ty+20, C_ACC_DIM);
                String b = String.valueOf(enCat);
                c.drawText(textRenderer, b, px+SW-13-textRenderer.getWidth(b)/2, ty+9, C_WHITE, false);
            }
            ty += 28;
        }

        ty += 4;
        c.fill(px+8, ty, px+SW-8, ty+1, C_SEP);
        ty += 8;
        c.drawText(textRenderer, "OTHER", px+10, ty, C_DGRAY, false);
        ty += 14;
        for (Module.Category cat : new Module.Category[]{Module.Category.CLIENT}) {
            boolean sel = !searchMode && cat == selCat;
            boolean hov = in(mx,my, px, ty, SW, 26);
            if (sel) { c.fill(px, ty, px+SW, ty+26, 0xFF18182A); c.fill(px, ty, px+3, ty+26, C_ACC); }
            else if (hov) { c.fill(px, ty, px+SW, ty+26, 0xFF131318); }
            c.drawText(textRenderer, cat.icon+" "+cat.label, px+12, ty+8, sel ? C_WHITE : C_GRAY, false);
            ty += 28;
        }
        String[] extra = {"👥 Friends", "⚙ Configs", "🖥 Hud"};
        for (String e2 : extra) {
            if (in(mx,my, px, ty, SW, 26)) c.fill(px, ty, px+SW, ty+26, 0xFF131318);
            c.drawText(textRenderer, e2, px+12, ty+8, C_DGRAY, false);
            ty += 28;
        }

        // User area
        c.fill(px, py+PH-34, px+SW, py+PH, 0xFF0A0A0D);
        c.fill(px, py+PH-35, px+SW, py+PH-34, C_SEP);
        drawSkull(c, px+8, py+PH-26);
        c.drawText(textRenderer, "Player", px+30, py+PH-28, C_GRAY, false);
        c.drawText(textRenderer, "UID: 1", px+30, py+PH-18, C_DGRAY, false);

        // Module cards
        List<Module> mods = searchMode && !searchQuery.isEmpty()
            ? GrimClientMod.core.moduleManager.search(searchQuery)
            : GrimClientMod.core.moduleManager.getByCategory(selCat);

        int cx0 = px+SW+8, cy0 = py+HH+8;
        int contW = PW-SW-16, contH = PH-HH-16;
        int cols = 2, cardW = (contW-6)/cols, cardH = 46, gap = 5;
        int rows = (mods.size()+cols-1)/cols;
        maxScroll = Math.max(0, rows*(cardH+gap)-contH+8);
        if (scroll > maxScroll) scroll = maxScroll;

        for (int i = 0; i < mods.size(); i++) {
            Module mod = mods.get(i);
            int col = i%cols, row = i/cols;
            int cx = cx0+col*(cardW+6);
            int cy = cy0+row*(cardH+gap)-scroll;
            if (cy+cardH < cy0 || cy > cy0+contH) continue;

            boolean on  = mod.isEnabled();
            boolean hov = in(mx,my, cx, cy, cardW, cardH);
            boolean isBound = bindingModule == mod;

            // Karta
            c.fill(cx, cy, cx+cardW, cy+cardH,
                isBound ? 0xFF1A1A3A : on ? C_CARD_ON : hov ? C_CARD_H : C_CARD);

            // Obramowanie jeśli bindujemy
            if (isBound) {
                c.fill(cx, cy, cx+cardW, cy+1, C_ACC);
                c.fill(cx, cy+cardH-1, cx+cardW, cy+cardH, C_ACC);
                c.fill(cx, cy, cx+1, cy+cardH, C_ACC);
                c.fill(cx+cardW-1, cy, cx+cardW, cy+cardH, C_ACC);
            }

            if (on) c.fill(cx, cy, cx+3, cy+cardH, C_ACC);
            c.fill(cx, cy, cx+cardW, cy+1, C_SEP);

            c.drawText(textRenderer, mod.getName(), cx+10, cy+9, on ? C_WHITE : C_GRAY, false);

            String desc = mod.getDescription();
            if (textRenderer.getWidth(desc) > cardW-20) {
                while (textRenderer.getWidth(desc+"..") > cardW-20 && desc.length()>0) desc=desc.substring(0,desc.length()-1);
                desc += "..";
            }
            c.drawText(textRenderer, desc, cx+10, cy+23, C_DGRAY, false);

            // Keybind label
            int kb = mod.getKeybind();
            if (kb != -1) {
                String keyName = InputUtil.fromKeyCode(kb, 0).getLocalizedText().getString();
                int kw = textRenderer.getWidth("["+keyName+"]");
                c.fill(cx+cardW-kw-10, cy+cardH-14, cx+cardW-6, cy+cardH-4, 0xFF1A1A2A);
                c.drawText(textRenderer, "["+keyName+"]", cx+cardW-kw-8, cy+cardH-13, C_ACC, false);
            }

            // Dot
            c.fill(cx+cardW-12, cy+9, cx+cardW-6, cy+15, on ? C_ACC : C_DGRAY);
        }

        // Scrollbar
        if (maxScroll > 0) {
            int sbX2=px+PW-4, sbY2=cy0, sbH2=contH;
            c.fill(sbX2, sbY2, sbX2+3, sbY2+sbH2, 0xFF111118);
            int th=Math.max(20, sbH2*sbH2/(sbH2+maxScroll));
            int tY=sbY2+(int)((float)scroll/maxScroll*(sbH2-th));
            c.fill(sbX2, tY, sbX2+3, tY+th, C_ACC);
        }

        // === KEYBIND POPUP ===
        if (waitingForKey && bindingModule != null) {
            int popW = 220, popH = 70;
            int popX = width/2 - popW/2;
            int popY = height/2 - popH/2;

            // Tło popup
            c.fill(popX-1, popY-1, popX+popW+1, popY+popH+1, C_POPUP_BORDER);
            c.fill(popX, popY, popX+popW, popY+popH, C_POPUP_BG);

            // Tytuł
            String title = "Bind: " + bindingModule.getName();
            c.drawText(textRenderer, title, popX+(popW-textRenderer.getWidth(title))/2, popY+12, C_WHITE, false);

            // Instrukcja
            String hint = "Wciśnij klawisz...";
            c.drawText(textRenderer, hint, popX+(popW-textRenderer.getWidth(hint))/2, popY+28, C_ACC, false);

            String esc = "[ESC] = usuń bind";
            c.drawText(textRenderer, esc, popX+(popW-textRenderer.getWidth(esc))/2, popY+44, C_DGRAY, false);

            // Aktualny bind
            if (bindingModule.getKeybind() != -1) {
                String cur = "Obecny: ["+InputUtil.fromKeyCode(bindingModule.getKeybind(), 0).getLocalizedText().getString()+"]";
                c.drawText(textRenderer, cur, popX+(popW-textRenderer.getWidth(cur))/2, popY+57, C_DGRAY, false);
            }
        }
    }

    // =========================================================
    //  INPUT
    // =========================================================
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        // Jeśli popup otwarty - klik zamknij
        if (waitingForKey) {
            waitingForKey = false;
            bindingModule = null;
            return true;
        }

        // Drag header
        if (btn == 0 && in((int)mx,(int)my, px, py, PW, HH)) {
            drag=true; dragX=(int)mx-px; dragY=(int)my-py; return true;
        }

        // Search box
        int sbx=px+SW+10, sby=py+(HH-18)/2;
        if (btn == 0 && in((int)mx,(int)my, sbx, sby, 190, 18)) {
            searchFocused=true; searchMode=true; return true;
        }
        if (searchFocused) searchFocused=false;

        // Kategoria
        if (!searchMode) {
            int tty = py+HH+22;
            for (Module.Category cat : new Module.Category[]{
                Module.Category.COMBAT, Module.Category.MOVEMENT,
                Module.Category.PLAYER, Module.Category.VISUALS
            }) {
                if (btn==0 && in((int)mx,(int)my, px, tty, SW, 26)) {
                    selCat=cat; scroll=0; searchMode=false; searchQuery=""; return true;
                }
                tty += 28;
            }
            tty += 12;
            if (btn==0 && in((int)mx,(int)my, px, tty, SW, 26)) {
                selCat=Module.Category.CLIENT; scroll=0; return true;
            }
        }

        // Moduły
        List<Module> mods = searchMode && !searchQuery.isEmpty()
            ? GrimClientMod.core.moduleManager.search(searchQuery)
            : GrimClientMod.core.moduleManager.getByCategory(selCat);

        int cx0=px+SW+8, cy0=py+HH+8;
        int contW=PW-SW-16, cols=2, cardW=(contW-6)/cols, cardH=46, gap=5;

        for (int i=0; i<mods.size(); i++) {
            int cx=cx0+(i%cols)*(cardW+6);
            int cy=cy0+(i/cols)*(cardH+gap)-scroll;
            if (in((int)mx,(int)my, cx, cy, cardW, cardH)) {
                if (btn == 0) {
                    // Lewy klik = toggle
                    mods.get(i).toggle();
                    return true;
                } else if (btn == 2) {
                    // Środkowy klik = otwórz keybind popup
                    bindingModule = mods.get(i);
                    waitingForKey = true;
                    return true;
                }
            }
        }

        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (drag) {
            px=Math.max(0,Math.min((int)mx-dragX, width-PW));
            py=Math.max(0,Math.min((int)my-dragY, height-PH));
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) { drag=false; return super.mouseReleased(mx,my,btn); }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scroll=Math.max(0,Math.min(maxScroll, scroll-(int)(v*12)));
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        // Jeśli czekamy na keybind
        if (waitingForKey && bindingModule != null) {
            if (key == 256) {
                // ESC = usuń bind
                bindingModule.setKeybind(-1);
            } else {
                // Przypisz klawisz
                bindingModule.setKeybind(key);
            }
            waitingForKey = false;
            bindingModule = null;
            return true;
        }

        if (key == 80 || key == 256) {
            if (searchFocused) { searchFocused=false; return true; }
            this.client.setScreen(null); return true;
        }
        if (searchFocused) {
            if (key==259 && !searchQuery.isEmpty()) {
                searchQuery=searchQuery.substring(0,searchQuery.length()-1);
                if (searchQuery.isEmpty()) searchMode=false;
                return true;
            }
            if (key==256) { searchFocused=false; searchMode=false; searchQuery=""; return true; }
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        if (searchFocused && c>=32) { searchQuery+=c; searchMode=true; scroll=0; return true; }
        return false;
    }

    private boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx>=x && mx<x+w && my>=y && my<y+h;
    }

    private void drawSkull(DrawContext c, int x, int y) {
        c.fill(x+2,y+1,  x+14,y+11, 0xFFDDDDCC);
        c.fill(x,  y+3,  x+2, y+9,  0xFFDDDDCC);
        c.fill(x+14,y+3, x+16,y+9,  0xFFDDDDCC);
        c.fill(x+2,y,    x+14,y+2,  0xFF111111);
        c.fill(x,  y+2,  x+16,y+4,  0xFF111111);
        c.fill(x+3,y+4,  x+6, y+7,  0xFF111111);
        c.fill(x+10,y+4, x+13,y+7,  0xFF111111);
        c.fill(x+4,y+5,  x+5, y+6,  0xFFCC2200);
        c.fill(x+11,y+5, x+12,y+6,  0xFFCC2200);
    }
}
