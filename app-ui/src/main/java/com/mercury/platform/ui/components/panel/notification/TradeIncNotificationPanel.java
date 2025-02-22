package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.config.descriptor.HotKeyPair;
import com.mercury.platform.shared.config.descriptor.HotKeyType;
import com.mercury.platform.shared.entity.message.TradeNotificationDescriptor;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.notification.controller.IncomingPanelController;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.TooltipConstants;

import javax.swing.*;
import java.awt.*;


public abstract class TradeIncNotificationPanel<T extends TradeNotificationDescriptor> extends TradeNotificationPanel<T, IncomingPanelController> {
    protected JPanel getHeader() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppThemeColor.MSG_HEADER);
        root.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        JPanel nickNamePanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        this.nicknameLabel = this.componentsFactory.getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_NICKNAME, TextAlignment.LEFTOP, 15f, this.getNicknameText());
        nicknameLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        JPanel headerPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        headerPanel.add(this.getExpandButton(), BorderLayout.LINE_START);

        JPanel nickLabelPanel = this.getNicknamePanel(this.nicknameLabel);

        headerPanel.add(nickLabelPanel, BorderLayout.CENTER);
        headerPanel.add(this.getForPanel("app/incoming_arrow.png"), BorderLayout.LINE_END);
        nickNamePanel.add(headerPanel, BorderLayout.LINE_START);
        root.add(nickNamePanel, BorderLayout.CENTER);

        JPanel opPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        JPanel interactionPanel = new JPanel(new GridLayout(1, 0, 4, 0));
        interactionPanel.setBackground(AppThemeColor.MSG_HEADER);
        JButton inviteButton = componentsFactory.getIconButton(IconConst.INVITE, 15, AppThemeColor.MSG_HEADER, TranslationKey.invite.value());
        inviteButton.addActionListener(e -> {
            this.controller.performInvite();
            root.setBorder(BorderFactory.createLineBorder(AppThemeColor.HEADER_SELECTED_BORDER));
        });
        JButton kickButton = componentsFactory.getIconButton(IconConst.KICK, 15, AppThemeColor.MSG_HEADER, TranslationKey.kick.value());
        kickButton.addActionListener(e -> {
            this.controller.performKickLeave(nicknameLabel.getText());
            if (this.notificationConfig.get().isDismissAfterKick()) {
                this.controller.performHide();
            }
        });
        JButton leaveButton = componentsFactory.getIconButton(IconConst.LEAVE, 16, AppThemeColor.MSG_HEADER, TranslationKey.leave.value());
        leaveButton.addActionListener(e -> {
            this.controller.performKickLeave(null);
            if (this.notificationConfig.get().isDismissAfterLeave()) {
                this.controller.performHide();
            }
        });
        JButton tradeButton = componentsFactory.getIconButton(IconConst.TRADE, 15, AppThemeColor.MSG_HEADER, TranslationKey.offer_trade.value());
        tradeButton.addActionListener(e -> {
            this.controller.performOfferTrade();
        });
        JButton openChatButton = componentsFactory.getIconButton(IconConst.CHAT_OPEN, 15, AppThemeColor.MSG_HEADER, TranslationKey.open_chat.value());
        openChatButton.addActionListener(e -> controller.performOpenChat());
        JButton whoIsButton = componentsFactory.getIconButton(IconConst.WHO_IS, 15, AppThemeColor.MSG_HEADER, TranslationKey.who_is.value());
        whoIsButton.addActionListener(e -> controller.performWhoIs());
        JButton hideButton = componentsFactory.getIconButton(IconConst.CLOSE, 15, AppThemeColor.MSG_HEADER, TranslationKey.close.value());
        hideButton.addActionListener(action -> {
            this.controller.performHide();
        });
        interactionPanel.add(inviteButton);
        interactionPanel.add(tradeButton);
        interactionPanel.add(kickButton);
        interactionPanel.add(leaveButton);
        interactionPanel.add(whoIsButton);
        interactionPanel.add(openChatButton);
        interactionPanel.add(hideButton);

        this.interactButtonMap.clear();
        this.interactButtonMap.put(HotKeyType.N_INVITE_PLAYER, inviteButton);
        this.interactButtonMap.put(HotKeyType.N_TRADE_PLAYER, tradeButton);
        this.interactButtonMap.put(HotKeyType.N_KICK_PLAYER, kickButton);
        this.interactButtonMap.put(HotKeyType.N_OPEN_CHAT, openChatButton);
        this.interactButtonMap.put(HotKeyType.N_CLOSE_NOTIFICATION, hideButton);

        JPanel timePanel = this.getTimePanel();
        opPanel.add(timePanel, BorderLayout.CENTER);
        opPanel.add(interactionPanel, BorderLayout.LINE_END);

        root.add(opPanel, BorderLayout.LINE_END);
        return root;
    }


    protected abstract JButton getStillInterestedButton();

    @Override
    protected void updateHotKeyPool() {
        this.hotKeysPool.clear();
        this.interactButtonMap.forEach((type, button) -> {
            HotKeyPair hotKeyPair = this.hotKeysConfig.get()
                    .getIncNHotKeysList()
                    .stream()
                    .filter(it -> it.getType().equals(type))
                    .findAny().orElse(null);
            if (!hotKeyPair.getDescriptor().getTitle().equals("...")) {
                this.hotKeysPool.put(hotKeyPair.getDescriptor(), button);
            }
        });
        this.initResponseButtonsPanel(this.notificationConfig.get().getButtons(), false);
        Window windowAncestor = SwingUtilities.getWindowAncestor(TradeIncNotificationPanel.this);
        if (windowAncestor != null) {
            windowAncestor.pack();
        }
    }
}
