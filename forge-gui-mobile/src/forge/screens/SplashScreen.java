package forge.screens;

import com.badlogic.gdx.Gdx;
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

/**
 * Schmister MTG full-screen loading screen.
 *
 * Visual artwork is loaded directly from the Android assets folder:
 * forge-gui-android/assets/schmister_mtg_splash.png
 *
 * Forge startup progress remains live underneath this presentation layer.
 */
public class SplashScreen extends FContainer {
    private TextureRegion splashTexture;
    private Texture splashBGTexture;
    private Texture schmisterTexture;
    private TextureRegion schmisterRegion;

    private FProgressBar progressBar;
    private FButton btnHome;

    private FSkinFont buttonFont;
    private FSkinFont versionFont;

    private boolean preparedForDialogs;
    private boolean showModeSelector;
    private boolean buttonCreated;
    private boolean ready;
    private boolean clear;

    private BGAnimation bgAnimation;

    private static final Color VERSION_COLOR = new Color(1.00f, 0.87f, 0.55f, 1f);

    public SplashScreen() {
        progressBar = getProgressBar();
        bgAnimation = getBgAnimation();
        loadSchmisterArtwork();
    }

    private void loadSchmisterArtwork() {
        try {
            schmisterTexture = new Texture(Gdx.files.internal("schmister_mtg_splash.png"));
            schmisterTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            schmisterRegion = new TextureRegion(schmisterTexture);
        } catch (Exception e) {
            // Fallback to Forge-provided splash texture if the custom asset is missing.
            schmisterTexture = null;
            schmisterRegion = null;
        }
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

    /** Called by Forge when startup has finished. */
    public void startClassic() {
        ready = true;
        progressBar.setDescription("Ready to play.");
        ensureEnterButton();
    }

    @Override
    protected void doLayout(float width, float height) {
        layoutControls(width, height);
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
        if (value) {
            ready = true;
            progressBar.setDescription("Ready to play.");
            ensureEnterButton();
        }
    }

    public boolean isShowModeSelector() {
        return showModeSelector;
    }

    private void ensureFonts() {
        if (buttonFont == null) {
            buttonFont = FSkinFont.get(22);
        }
        if (versionFont == null) {
            versionFont = FSkinFont.get(10);
        }

        if (Forge.forcedEnglishonCJKMissing && !clear) {
            clear = true;
            FSkinFont.preloadAll("");
            buttonFont = FSkinFont.get(22);
            versionFont = FSkinFont.get(10);
        }
    }

    private void ensureEnterButton() {
        ensureFonts();

        if (!buttonCreated) {
            buttonCreated = true;

            btnHome = new FButton("ENTER SCHMISTER MTG");
            btnHome.setFont(buttonFont);
            btnHome.setCommand(e -> {
                Forge.openHomeDefault();
                Forge.clearSplashScreen();
            });

            add(btnHome);
        }

        btnHome.setVisible(true);
        layoutControls(getWidth(), getHeight());
    }

    /**
     * Cover-fit: artwork fills the whole phone screen with no letterboxing.
     */
    private void drawCover(Graphics g, TextureRegion image, float width, float height) {
        if (image == null) {
            g.fillRect(Color.BLACK, 0, 0, width, height);
            return;
        }

        float imageRatio = (float) image.getRegionWidth() / (float) image.getRegionHeight();
        float screenRatio = width / height;

        float drawX;
        float drawY;
        float drawW;
        float drawH;

        if (imageRatio > screenRatio) {
            drawH = height;
            drawW = drawH * imageRatio;
            drawX = (width - drawW) / 2f;
            drawY = 0f;
        } else {
            drawW = width;
            drawH = drawW / imageRatio;
            drawX = 0f;
            drawY = (height - drawH) / 2f;
        }

        g.drawImage(image, drawX, drawY, drawW, drawH);
    }

    /**
     * Positions live Forge controls over the matching ornate frames in the art.
     */
    private void layoutControls(float width, float height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        // Loading bar frame in the generated artwork.
        float progressX = width * 0.175f;
        float progressY = height * 0.615f;
        float progressW = width * 0.650f;
        float progressH = height * 0.058f;
        progressBar.setBounds(progressX, progressY, progressW, progressH);

        if (btnHome != null) {
            // Enter-button frame in the generated artwork.
            float buttonW = width * 0.530f;
            float buttonH = height * 0.105f;
            float buttonX = (width - buttonW) / 2f;
            float buttonY = height * 0.735f;
            btnHome.setBounds(buttonX, buttonY, buttonW, buttonH);
        }
    }

    private class BGAnimation extends ForgeAnimation {
        @Override
        protected boolean advance(float dt) {
            // Keep animation object alive while Forge starts up.
            return !ready;
        }

        @Override
        protected void onEnd(boolean endingAll) {
            // User enters through the on-screen button.
        }
    }

    @Override
    protected void drawBackground(Graphics g) {
        ensureFonts();

        float width = getWidth();
        float height = getHeight();

        TextureRegion art = schmisterRegion != null ? schmisterRegion : splashTexture;
        drawCover(g, art, width, height);
        layoutControls(width, height);

        // Keep Forge's real loading status and percentage live.
        g.draw(progressBar);

        if (ready || showModeSelector) {
            progressBar.setDescription("Ready to play.");
            ensureEnterButton();
        }

        // Live engine build text at the bottom of the art.
        String version = "Engine build " + Forge.getDeviceAdapter().getVersionString();
        g.drawText(
                version,
                versionFont,
                VERSION_COLOR,
                0,
                height * 0.900f,
                width,
                height * 0.035f,
                false,
                Align.center,
                true
        );
    }
}
