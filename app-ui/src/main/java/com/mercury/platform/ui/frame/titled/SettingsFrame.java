package com.mercury.platform.ui.frame.titled;


import com.google.gson.Gson;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.MercuryConstants;
import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.config.descriptor.adr.AdrComponentType;
import com.mercury.platform.shared.config.descriptor.adr.AdrDurationComponentDescriptor;
import com.mercury.platform.shared.config.descriptor.adr.AdrProgressBarDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.settings.MenuPanel;
import com.mercury.platform.ui.components.panel.settings.page.GlobalHotkeyGroup;
import com.mercury.platform.ui.dialog.AlertDialog;
import com.mercury.platform.ui.dialog.OkDialog;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.UpdateCheck;
import com.mercury.platform.ui.misc.note.Note;
import com.mercury.platform.ui.misc.note.NotesLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SettingsFrame extends AbstractTitledComponentFrame {
    private final static Logger logger = LogManager.getLogger(SettingsFrame.class.getSimpleName());
    private JPanel currentPanel;
    private JPanel root;
    private Gson gson = new Gson();

    public SettingsFrame() {
        super();
        this.setAlwaysOnTop(false);
        this.processingHideEvent = false;
        this.processHideEffect = false;
        this.setFocusable(true);
        this.setFocusableWindowState(true);
        this.setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    public void onViewInit() {
        this.root = new JPanel(new BorderLayout());
        this.setBackground(AppThemeColor.FRAME);
        MenuPanel menuPanel = new MenuPanel();
        JPanel leftPanel = this.componentsFactory.getJPanel(new BorderLayout());
        leftPanel.add(menuPanel, BorderLayout.CENTER);
        leftPanel.add(this.getOperationsButtons(), BorderLayout.PAGE_END);
        this.add(leftPanel, BorderLayout.LINE_START);
        this.add(this.root, BorderLayout.CENTER);
        this.add(this.getBottomPanel(), BorderLayout.PAGE_END);
        this.pack();
    }

    @Override
    public void onSizeChange() {
        super.onSizeChange();
        FrameDescriptor frameDescriptor = this.framesConfig.get(this.getClass().getSimpleName());
        this.setPreferredSize(frameDescriptor.getFrameSize());
    }

    public void setContentPanel(JPanel panel) {
        if (currentPanel != null) {
            this.root.removeAll();
        }
        this.root.add(panel, BorderLayout.CENTER);
        if (!SystemUtils.IS_OS_WINDOWS) {
            this.getContentPane().setBackground(AppThemeColor.SETTINGS_BG);
            this.setBackground(AppThemeColor.SETTINGS_BG);
        }
        this.currentPanel = panel;
        this.pack();
        this.repaint();
    }

    private JPanel getBottomPanel() {
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout());
        root.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppThemeColor.MSG_HEADER_BORDER));
        root.setBackground(AppThemeColor.ADR_FOOTER_BG);

        AdrDurationComponentDescriptor donateDescriptor = new AdrProgressBarDescriptor();
        donateDescriptor.setIconEnable(false);
        donateDescriptor.setDuration(50d);
        donateDescriptor.setSize(new Dimension(100, 20));
        donateDescriptor.setType(AdrComponentType.PROGRESS_BAR);
        donateDescriptor.setCustomTextEnable(true);
        donateDescriptor.setCustomText("0$");
        donateDescriptor.setFontSize(21);
        donateDescriptor.setLowValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setMediumValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setDefaultValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setBorderColor(AppThemeColor.ADR_DEFAULT_BORDER);
        donateDescriptor.setBackgroundColor(AppThemeColor.FRAME);
        donateDescriptor.setForegroundColor(AppThemeColor.BUTTON);
//        MercuryTracker tracker = new MercuryTracker(donateDescriptor);
//        tracker.setValue(1000);
//        tracker.setPreferredSize(donateDescriptor.getSize());
//        root.add(this.componentsFactory.getTextLabel("Monthly donations:", FontStyle.BOLD, 16), BorderLayout.LINE_START);
//        root.add(this.componentsFactory.wrapToSlide(tracker, AppThemeColor.ADR_FOOTER_BG, 2, 2, 2, 1), BorderLayout.CENTER);
        root.add(this.getSaveButtonPanel(), BorderLayout.LINE_END);
        return root;
    }

    private JPanel getSaveButtonPanel() {
        JPanel root = componentsFactory.getTransparentPanel(new GridLayout(1, 0));
        JButton saveButton = componentsFactory.getBorderedButton(TranslationKey.save.value(), 16);
        saveButton.addActionListener(e -> {
            MercuryStoreUI.settingsSaveSubject.onNext(true);
            MercuryStoreCore.showingDelaySubject.onNext(true);
            this.hideComponent();
        });
        JButton cancelButton = componentsFactory.getButton(
                FontStyle.BOLD,
                AppThemeColor.FRAME,
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                TranslationKey.cancel.value(),
                16f);
        cancelButton.addActionListener(e -> {
            GlobalHotkeyGroup.INSTANCE.clear();
            MercuryStoreCore.showingDelaySubject.onNext(true);
            this.hideComponent();
            MercuryStoreUI.settingsRestoreSubject.onNext(true);
        });

        JButton donate = componentsFactory.getIconButton("app/paypal.png", 70f, AppThemeColor.ADR_FOOTER_BG, "Donate");
        donate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.paypal.me/Morph21MT"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        saveButton.setPreferredSize(new Dimension(110, 26));
        cancelButton.setPreferredSize(new Dimension(110, 26));
        root.add(this.componentsFactory.wrapToSlide(donate, AppThemeColor.HEADER, 2, 2, 2, 2));
        root.add(this.componentsFactory.wrapToSlide(cancelButton, AppThemeColor.HEADER, 2, 2, 2, 2));
        root.add(this.componentsFactory.wrapToSlide(saveButton, AppThemeColor.HEADER, 2, 2, 2, 2));
        return root;
    }

    private JPanel getOperationsButtons() {
        JPanel root = componentsFactory.getTransparentPanel(new GridLayout(0, 1, 4, 2));
        root.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppThemeColor.BORDER));
        JButton openTutorial = this.getOperationButton(TranslationKey.open_tutorial.value(), "app/tutorial.png");
        openTutorial.addActionListener(action -> {
            FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
            FramesManager.INSTANCE.showFrame(NotesFrame.class);
        });
        JButton checkUpdates = this.getOperationButton(TranslationKey.check_for_updates.value(), "app/check-update.png");

        checkUpdates.addActionListener(action -> {
            checkUpdates.setText(TranslationKey.hamsters_are_running.value());
            checkUpdates.setEnabled(false);
            repaint();
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    UpdateCheck.checkForUpdates(false);
//                    SettingsFrame.this.checkForUpdates();
                    checkUpdates.setText(TranslationKey.check_for_updates.value());
                    checkUpdates.setEnabled(true);
                    return null;
                }
            };
            worker.execute();
        });
        JButton openTests = this.getOperationButton(TranslationKey.open_tests.value(), "app/open-tests.png");
        openTests.addActionListener(action -> {
            FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
            FramesManager.INSTANCE.showFrame(TestCasesFrame.class);
//            FramesManager.INSTANCE.preShowFrame(TestCasesFrame.class);
        });
        root.add(this.componentsFactory.wrapToSlide(openTutorial));
        root.add(this.componentsFactory.wrapToSlide(checkUpdates));
        root.add(this.componentsFactory.wrapToSlide(openTests));

        JButton patchNotes = componentsFactory.getBorderedButton(TranslationKey.open_patch_notes.value());
        patchNotes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    NotesLoader notesLoader = new NotesLoader();
                    java.util.List<Note> patchNotes = notesLoader.getPatchNotes();
                    if (patchNotes.size() != 0) {
                        NotesFrame patchNotesFrame = new NotesFrame(patchNotes, NotesFrame.NotesType.PATCH);
                        patchNotesFrame.init();
                        patchNotesFrame.showComponent();
                    }
                }
            }
        });
        return root;
    }

    private JButton getOperationButton(String title, String iconPath) {
        JButton button = this.componentsFactory.getButton(title);
        button.setPreferredSize(new Dimension(210, 35));
        button.setForeground(AppThemeColor.TEXT_DEFAULT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(AppThemeColor.ADR_BG);
        button.setFont(this.componentsFactory.getFont(FontStyle.BOLD, 16f));
        button.setIcon(this.componentsFactory.getIcon(iconPath, 22));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AppThemeColor.ADR_PANEL_BORDER),
                BorderFactory.createEmptyBorder(2, 10, 2, 2)));
        return button;
    }

    @Override
    public void subscribe() {
        MercuryStoreUI.settingsRepaintSubject.subscribe(state -> {
            this.repaint();
        });
        MercuryStoreUI.settingsPackSubject.subscribe(state -> {
            this.pack();
        });
        MercuryStoreUI.adrManagerRepaint.subscribe(state -> {
            this.repaint();
        });
    }

    @Override
    protected String getFrameTitle() {
        return TranslationKey.settings.value();
    }

    @Override
    public void hideComponent() {
        super.hideComponent();
        MercuryStoreCore.showingDelaySubject.onNext(true);
    }

}
