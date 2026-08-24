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

    private static final Color BG = new Color(0.025f, 0.035f, 0.050f, 1f);
    private static final Color PANEL = new Color(0.055f, 0.075f, 0.100f, 1f);
    private static final Color PANEL_SOFT = new Color(0.075f, 0.100f, 0.130f, 1f);
    private static final Color ACCENT = new Color(0.25f, 0.62f, 0.95f, 1f);
    private static final Color TEXT = new Color(0.94f, 0.97f, 1f, 1f);
    private static final Color MUTED = new Color(0.60f, 0.68f, 0.76f, 1f);

    public SplashScreen() {
        progressBar = getProgressBar();
        bgAnimation = getBgAnimation();
    }

    public BGAnimation getBgAnimation() {
        if (bgAnimation == null) {
            bgAnimation = new BGAnimation();
        }
        return bgAnimation;
    }

    public FProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new FProgressBar();
            progressBar.setDescription("Preparing Commander engine...");
        }
        return progressBar;
    }

    public void setSplashTexture(TextureRegion textureRegion) {
        splashTexture = textureRegion;
    }

    public void setSplashBGTexture(Texture texture) {
        splashBGTexture = texture;
    }

    public void startClassic() {
        startClassic = true;
        hideBtn = true;
        bgAnimation.DURATION = 0.55f;
        bgAnimation.progress = 0f;
        bgAnimation.openAdventure = false;
    }

    @Override
    protected void doLayout(float width, float height) {
    }

    public void prepareForDialogs() {
        if (preparedForDialogs) {
            return;
        }

        Color defaultColor = new Color(0, 0, 0, 0);
        for (final FSkinColor.Colors c : FSkinColor.Colors.values()) {
            switch (c) {
                case CLR_BORDERS:
                case CLR_TEXT:
                    c.setColor(FProgressBar.SEL_FORE_COLOR);
                    break;
                case CLR_ACTIVE:
                case CLR_THEME2:
                    c.setColor(FProgressBar.SEL_BACK_COLOR);
                    break;
                case CLR_INACTIVE:
                    c.setColor(FSkinColor.stepColor(FProgressBar.SEL_BACK_COLOR, -80));
                    break;
                default:
                    c.setColor(defaultColor);
                    break;
            }
        }
        FSkinColor.updateAll();
        preparedForDialogs = true;
    }

    public void setShowModeSelector(boolean value) {
        showModeSelector = value;
    }

    public boolean isShowModeSelector() {
        return showModeSelector;
    }

    private void ensureFonts() {
        if (titleFont == null) titleFont = FSkinFont.get(32);
        if (subtitleFont == null) subtitleFont = FSkinFont.get(16);
        if (statusFont == null) statusFont = FSkinFont.get(12);
        if (smallFont == null) smallFont = FSkinFont.get(9);

        if (Forge.forcedEnglishonCJKMissing && !clear) {
            clear = true;
            FSkinFont.preloadAll("");
            titleFont = FSkinFont.get(32);
            subtitleFont = FSkinFont.get(16);
            statusFont = FSkinFont.get(12);
            smallFont = FSkinFont.get(9);
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
                btnHome.setTop(y + (getHeight() / 18f * percentage));
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
                if (openAdventure) {
                    Forge.openAdventure();
                } else {
                    Forge.openHomeDefault();
                }
                Forge.clearSplashScreen();
            }
        }
    }

    @Override
    protected void drawBackground(Graphics g) {
        bgAnimation.start();
        bgAnimation.drawBackground(g);
    }

    private void drawSchmisterSplash(Graphics g, float alpha, boolean showEnterButton) {
        ensureFonts();

        float oldAlpha = g.getfloatAlphaComposite();
        g.setAlphaComposite(alpha);

        float w = getWidth();
        float h = getHeight();

        g.fillRect(BG, 0, 0, w, h);

        float panelW = Forge.isLandscapeMode() ? w * 0.54f : w * 0.86f;
        float panelH = Forge.isLandscapeMode() ? h * 0.68f : h * 0.58f;
        float panelX = (w - panelW) / 2f;
        float panelY = (h - panelH) / 2f;

        g.fillRect(PANEL, panelX, panelY, panelW, panelH);

        float railH = Math.max(3f, h * 0.006f);
        g.fillRect(ACCENT, panelX, panelY, panelW, railH);

        float inset = Math.max(10f, panelW * 0.035f);
        g.fillRect(PANEL_SOFT, panelX + inset, panelY + inset * 1.6f,
                panelW - inset * 2f, panelH - inset * 3.0f);

        float titleY = panelY + panelH * 0.22f;
        float titleH = panelH * 0.18f;

        g.drawText("SCHMISTER MTG", titleFont, TEXT,
                panelX + inset, titleY, panelW - inset * 2f, titleH,
                false, Align.center, true);

        g.drawText("COMMANDER PLAYTESTING", subtitleFont, ACCENT,
                panelX + inset, titleY + titleH * 0.78f,
                panelW - inset * 2f, titleH * 0.55f,
                false, Align.center, true);

        float featureY = panelY + panelH * 0.49f;
        float featureH = panelH * 0.09f;

        g.drawText("BUILD  •  PLAY  •  TEST", statusFont, MUTED,
                panelX + inset, featureY, panelW - inset * 2f, featureH,
                false, Align.center, true);

        float pbW = panelW * 0.68f;
        float pbH = Math.max(22f, panelH * 0.075f);
        float pbX = panelX + (panelW - pbW) / 2f;
        float pbY = panelY + panelH * 0.67f;

        progressBar.setBounds(pbX, pbY, pbW, pbH);
        g.draw(progressBar);

        String version = "Engine build " + Forge.getDeviceAdapter().getVersionString();
        g.drawText(version, smallFont, MUTED,
                panelX + inset, panelY + panelH - inset * 1.9f,
                panelW - inset * 2f, smallFont.getLineHeight() * 1.5f,
                false, Align.center, true);

        if (showEnterButton) {
            ensureEnterButton(panelX, panelY, panelW, panelH);
        }

        g.setAlphaComposite(oldAlpha);
    }

    private void ensureEnterButton(float panelX, float panelY, float panelW, float panelH) {
        if (!init) {
            init = true;

            btnHome = new FButton("ENTER SCHMISTER MTG");
            btnHome.setFont(FSkinFont.get(18));
            btnHome.setCommand(e -> {
                hideBtn = true;
                startClassic = true;
                bgAnimation.progress = 0f;
                bgAnimation.openAdventure = false;
            });

            add(btnHome);
        }

        float btnW = panelW * 0.58f;
        float btnH = Math.max(44f, panelH * 0.09f);
        float btnX = panelX + (panelW - btnW) / 2f;
        float btnY = panelY + panelH * 0.79f;

        btnHome.setBounds(btnX, btnY, btnW, btnH);
    }
}
