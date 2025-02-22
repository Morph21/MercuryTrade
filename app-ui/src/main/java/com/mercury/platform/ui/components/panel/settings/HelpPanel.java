package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.frame.titled.NotesFrame;
import com.mercury.platform.ui.frame.titled.SettingsFrame;
import com.mercury.platform.ui.frame.titled.TestCasesFrame;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.note.Note;
import com.mercury.platform.ui.misc.note.NotesLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpPanel extends JPanel implements ViewInit {
    private ComponentsFactory componentsFactory;

    public HelpPanel() {
        super();
        componentsFactory = ComponentsFactory.INSTANCE;
        this.setBackground(AppThemeColor.TRANSPARENT);
        onViewInit();
    }

    @Override
    public void onViewInit() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton openTutorial = componentsFactory.getBorderedButton(TranslationKey.open_tutorial.value());
        openTutorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
                    FramesManager.INSTANCE.showFrame(NotesFrame.class);
                }
            }
        });
        JButton openTests = componentsFactory.getBorderedButton(TranslationKey.open_tests.value());
        openTests.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
                    FramesManager.INSTANCE.preShowFrame(TestCasesFrame.class);
                    MercuryStoreCore.frameVisibleSubject.onNext(FrameVisibleState.SHOW);
                }
            }
        });
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
        JButton updateEvent = componentsFactory.getBorderedButton(TranslationKey.update_event.value());
        updateEvent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    MercuryStoreCore.updateInfoSubject.onNext(123);
                    FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
                }
            }
        });
        this.add(openTutorial);
        this.add(openTests);
        this.add(patchNotes);
        this.add(updateEvent);
    }
}
