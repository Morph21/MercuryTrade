package com.mercury.platform.ui.components.panel.grid;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.config.descriptor.StashTabDescriptor;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class TabInfoPanel extends JPanel implements ViewInit {
    private ComponentsFactory componentsFactory;
    private StashTabDescriptor stashTabDescriptor;

    public TabInfoPanel(@NonNull StashTabDescriptor stashTabDescriptor, @NonNull ComponentsFactory componentsFactory) {
        this.stashTabDescriptor = stashTabDescriptor;
        this.componentsFactory = componentsFactory;
        onViewInit();
    }

    @Override
    public void onViewInit() {
        this.setLayout(new BorderLayout());
        this.setBackground(AppThemeColor.FRAME);
        JButton hideButton = componentsFactory.getIconButton(IconConst.CLOSE, 12, AppThemeColor.FRAME, TranslationKey.dismiss.value());
        hideButton.addActionListener((action) -> {
            stashTabDescriptor.setUndefined(true);
            MercuryStoreUI.dismissTabInfoPanelSubject.onNext(this);
        });
        JPanel tabInfoPanel = componentsFactory.getJPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel tabLabel = componentsFactory.getTextLabel(stashTabDescriptor.getTitle());
        tabLabel.setBorder(null);
        tabLabel.setFont(componentsFactory.getFont(FontStyle.BOLD, 15f));
        tabInfoPanel.add(tabLabel);
        JCheckBox isItQuad = componentsFactory.getCheckBox(TranslationKey.is_it_quad.value());
        isItQuad.setSelected(stashTabDescriptor.isQuad());
        isItQuad.setPreferredSize(new Dimension(16, 16));
        isItQuad.addActionListener(action -> {
            stashTabDescriptor.setQuad(isItQuad.isSelected());
        });
        tabInfoPanel.add(isItQuad);
        tabInfoPanel.add(hideButton);

        this.add(tabInfoPanel, BorderLayout.PAGE_END);
        this.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER, 1));
    }

    public StashTabDescriptor getStashTabDescriptor() {
        return stashTabDescriptor;
    }

    public void setStashTabDescriptor(StashTabDescriptor stashTabDescriptor) {
        this.stashTabDescriptor = stashTabDescriptor;
    }
}
