package forge.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

import forge.Forge;
import forge.Graphics;
import forge.animation.ForgeAnimation;
import forge.assets.FSkinColor;
import forge.assets.FSkinFont;
import forge.toolbox.FButton;
import forge.toolbox.FContainer;
import forge.toolbox.FProgressBar;

public class SplashScreen extends FContainer {
    private TextureRegion splashTexture;
    private Texture splashBGTexture;
    private FProgressBar progressBar;
    private FSkinFont titleFont, subtitleFont, statusFont, smallFont;
    private boolean preparedForDialogs, showModeSelector, init, hideBtn, startClassic, clear;
    private FButton btnHome;
    private BGAnimation bgAnimation;

    private static final Color BG = new Color(0.012f, 0.014f, 0.020f, 1f);
    private static final Color SCRIM = new Color(0.015f, 0.018f, 0.025f, 0.72f);
    private static final Color PANEL = new Color(0.030f, 0.035f, 0.047f, 0.82f);
    private static final Color PANEL_INNER = new Color(0.055f, 0.060f, 0.075f, 0.78f);
    private static final Color BORDER = new Color(0.55f, 0.50f, 0.38f, 0.95f);
    private static final Color TEXT = new Color(0.97f, 0.95f, 0.89f, 1f);
    private static final Color MUTED = new Color(0.72f, 0.70f, 0.66f, 1f);
    private static final Color WHITE = new Color(0.91f, 0.85f, 0.66f, 1f);
    private static final Color BLUE = new Color(0.18f, 0.48f, 0.82f, 1f);
    private static final Color BLACK = new Color(0.30f, 0.25f, 0.34f, 1f);
    private static final Color RED = new Color(0.75f, 0.20f, 0.15f, 1f);
    private static final Color GREEN = new Color(0.18f, 0.48f, 0.26f, 1f);

    public SplashScreen() {
        progressBar = getProgressBar();
        bgAnimation = getBgAnimation();
    }

    public BGAnimation getBgAnimation() {
        if (bgAnimation == null) bgAnimation = new BGAnimation();
        return bgAnimation;
    }

    public FProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new FProgressBar();
            progressBar.setDescription("Preparing Commander engine...");
        }
        return progressBar;
    }

    public void setSplashTexture(TextureRegion textureRegion) { splashTexture = textureRegion; }
    public void setSplashBGTexture(Texture texture) { splashBGTexture = texture; }

    public void startClassic() {
        startClassic = true;
        hideBtn = true;
        bgAnimation.DURATION = 0.55f;
        bgAnimation.progress = 0f;
        bgAnimation.openAdventure = false;
    }

    @Override
    protected void doLayout(float width, float height) { }

    public void prepareForDialogs() {
        if (preparedForDialogs) return;
        Color defaultColor = new Color(0, 0, 0, 0);
        for (final FSkinColor.Colors c : FSkinColor.Colors.values()) {
            switch (c) {
                case CLR_BORDERS:
                case CLR_TEXT:
                    c.setColor(FProgressBar.SEL_FORE_COLOR); break;
                case CLR_ACTIVE:
                case CLR_THEME2:
                    c.setColor(FProgressBar.SEL_BACK_COLOR); break;
                case CLR_INACTIVE:
                    c.setColor(FSkinColor.stepColor(FProgressBar.SEL_BACK_COLOR, -80)); break;
                default:
                    c.setColor(defaultColor); break;
            }
        }
        FSkinColor.updateAll();
        preparedForDialogs = true;
    }

    public void setShowModeSelector(boolean value) { showModeSelector = value; }
    public boolean isShowModeSelector() { return showModeSelector; }

    private void ensureFonts() {
        if (titleFont == null) titleFont = FSkinFont.get(40);
        if (subtitleFont == null) subtitleFont = FSkinFont.get(18);
        if (statusFont == null) statusFont = FSkinFont.get(14);
        if (smallFont == null) smallFont = FSkinFont.get(10);
        if (Forge.forcedEnglishonCJKMissing && !clear) {
            clear = true;
            FSkinFont.preloadAll("");
            titleFont = FSkinFont.get(40);
            subtitleFont = FSkinFont.get(18);
            statusFont = FSkinFont.get(14);
            smallFont = FSkinFont.get(10);
        }
    }

    private class BGAnimation extends ForgeAnimation {
        float DURATION = 0.8f;
        private float progress = 0f;
        private boolean openAdventure;

        public void drawBackground(Graphics g) {
            float percentage = Math.max(0f, Math.min(1f, progress / DURATION));
            if (startClassic) {
                drawSchmisterSplash(g, 1f - percentage, false);
                return;
            }
            drawSchmisterSplash(g, 1f, showModeSelector);
            if (hideBtn && btnHome != null) {
                float y = btnHome.getTop();
                btnHome.setTop(y + getHeight() * 0.08f * percentage);
            }
        }

        @Override
        protected boolean advance(float dt) {
            progress += dt;
            return progress < DURATION;
        }

        @Override
        protected void onEnd(boolean endingAll) {
            if (startClassic || hideBtn) {
                if (openAdventure) Forge.openAdventure();
                else Forge.openHomeDefault();
                Forge.clearSplashScreen();
            }
        }
    }

    @Override
    protected void drawBackground(Graphics g) {
        bgAnimation.start();
        bgAnimation.drawBackground(g);
    }

    private void drawCoverImage(Graphics g, TextureRegion image, float width, float height) {
        if (image == null) {
            g.fillRect(BG, 0, 0, width, height);
            return;
        }
        float imageRatio = (float) image.getRegionWidth() / (float) image.getRegionHeight();
        float screenRatio = width / height;
        float x, y, w, h;
        if (imageRatio > screenRatio) {
            h = height;
            w = h * imageRatio;
            x = (width - w) / 2f;
            y = 0f;
        } else {
            w = width;
            h = w / imageRatio;
            x = 0f;
            y = (height - h) / 2f;
        }
        g.drawImage(image, x, y, w, h);
    }

    private void drawManaRail(Graphics g, float x, float y, float width, float height) {
        float segment = width / 5f;
        g.fillRect(WHITE, x, y, segment, height);
        g.fillRect(BLUE, x + segment, y, segment, height);
        g.fillRect(BLACK, x + segment * 2f, y, segment, height);
        g.fillRect(RED, x + segment * 3f, y, segment, height);
        g.fillRect(GREEN, x + segment * 4f, y, width - segment * 4f, height);
    }

    private void drawSchmisterSplash(Graphics g, float alpha, boolean showEnterButton) {
        ensureFonts();
        float oldAlpha = g.getfloatAlphaComposite();
        g.setAlphaComposite(alpha);
        final float w = getWidth();
        final float h = getHeight();

        g.fillRect(BG, 0, 0, w, h);
        drawCoverImage(g, splashTexture, w, h);
        g.fillRect(SCRIM, 0, 0, w, h);

        float manaRailH = Math.max(5f, h * 0.012f);
        drawManaRail(g, 0f, 0f, w, manaRailH);
        drawManaRail(g, 0f, h - manaRailH, w, manaRailH);

        float panelW = Forge.isLandscapeMode() ? w * 0.88f : w * 0.92f;
        float panelH = Forge.isLandscapeMode() ? h * 0.82f : h * 0.82f;
        float panelX = (w - panelW) / 2f;
        float panelY = (h - panelH) / 2f;

        g.fillRect(PANEL, panelX, panelY, panelW, panelH);

        float frame = Math.max(2f, h * 0.005f);
        g.fillRect(BORDER, panelX, panelY, panelW, frame);
        g.fillRect(BORDER, panelX, panelY + panelH - frame, panelW, frame);
        g.fillRect(BORDER, panelX, panelY, frame, panelH);
        g.fillRect(BORDER, panelX + panelW - frame, panelY, frame, panelH);

        float inset = Math.max(12f, panelW * 0.022f);
        g.fillRect(PANEL_INNER, panelX + inset, panelY + inset,
                panelW - inset * 2f, panelH - inset * 2f);

        float titleY = panelY + panelH * 0.11f;
        float titleH = panelH * 0.22f;
        g.drawText("SCHMISTER MTG", titleFont, TEXT,
                panelX + inset, titleY, panelW - inset * 2f, titleH,
                false, Align.center, true);

        g.drawText("COMMANDER PLAYTESTING", subtitleFont, WHITE,
                panelX + inset, titleY + titleH * 0.72f,
                panelW - inset * 2f, titleH * 0.42f,
                false, Align.center, true);

        float dividerW = panelW * 0.54f;
        float dividerX = panelX + (panelW - dividerW) / 2f;
        float dividerY = panelY + panelH * 0.40f;
        float dividerH = Math.max(4f, h * 0.008f);
        drawManaRail(g, dividerX, dividerY, dividerW, dividerH);

        g.drawText("BUILD  •  PLAY  •  TEST", statusFont, MUTED,
                panelX + inset, panelY + panelH * 0.45f,
                panelW - inset * 2f, panelH * 0.08f,
                false, Align.center, true);

        float pbW = panelW * 0.72f;
        float pbH = Math.max(36f, panelH * 0.09f);
        float pbX = panelX + (panelW - pbW) / 2f;
        float pbY = panelY + panelH * 0.59f;
        progressBar.setBounds(pbX, pbY, pbW, pbH);
        g.draw(progressBar);

        if (showEnterButton) ensureEnterButton(panelX, panelY, panelW, panelH);

        String version = "Engine build " + Forge.getDeviceAdapter().getVersionString();
        g.drawText(version, smallFont, MUTED,
                panelX + inset, panelY + panelH - inset * 1.7f,
                panelW - inset * 2f, smallFont.getLineHeight() * 1.5f,
                false, Align.center, true);

        g.setAlphaComposite(oldAlpha);
    }

    private void ensureEnterButton(float panelX, float panelY, float panelW, float panelH) {
        if (!init) {
            init = true;
            btnHome = new FButton("ENTER SCHMISTER MTG");
            btnHome.setFont(FSkinFont.get(24));
            btnHome.setCommand(e -> {
                hideBtn = true;
                startClassic = true;
                bgAnimation.progress = 0f;
                bgAnimation.openAdventure = false;
            });
            add(btnHome);
        }

        float btnW = panelW * 0.62f;
        float btnH = Math.max(56f, panelH * 0.12f);
        float btnX = panelX + (panelW - btnW) / 2f;
        float btnY = panelY + panelH * 0.75f;
        btnHome.setBounds(btnX, btnY, btnW, btnH);
    }
}
