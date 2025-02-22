package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.HotKeyPair;
import com.mercury.platform.shared.config.descriptor.HotKeyType;
import com.mercury.platform.shared.config.descriptor.ScannerDescriptor;
import com.mercury.platform.shared.entity.message.PlainMessageDescriptor;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.notification.controller.ScannerPanelController;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.TooltipConstants;

import javax.swing.*;
import java.awt.*;


public class ScannerNotificationPanel extends NotificationPanel<PlainMessageDescriptor, ScannerPanelController> {
    private PlainConfigurationService<ScannerDescriptor> config;

    @Override
    public void onViewInit() {
        super.onViewInit();
        this.config = Configuration.get().scannerConfiguration();
        JLabel sourceLabel = this.componentsFactory.getTextLabel(this.data.getMessage(), FontStyle.REGULAR, 17f);
        sourceLabel.setBackground(AppThemeColor.FRAME);
        sourceLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sourceLabel.setVerticalAlignment(SwingConstants.TOP);
        this.contentPanel = this.componentsFactory.wrapToSlide(sourceLabel, AppThemeColor.FRAME, 2, 2, 2, 2);
        switch (this.notificationConfig.get().getFlowDirections()) {
            case DOWNWARDS: {
                this.add(this.getHeader(), BorderLayout.PAGE_START);
                break;
            }
            case UPWARDS: {
                this.add(this.getHeader(), BorderLayout.PAGE_END);
                break;
            }
        }
        this.contentPanel.setPreferredSize(new Dimension(10, (int) (60 * this.componentsFactory.getScale())));
        this.add(this.contentPanel, BorderLayout.CENTER);
        this.updateHotKeyPool();
    }

    private JPanel getHeader() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppThemeColor.MSG_HEADER);

        JPanel nickNamePanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        JLabel nicknameLabel = this.componentsFactory.getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_NICKNAME, TextAlignment.LEFTOP, 15f, this.data.getNickName());
        nicknameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        nickNamePanel.add(this.getExpandButton(), BorderLayout.LINE_START);
        nickNamePanel.add(nicknameLabel, BorderLayout.CENTER);
        root.add(nickNamePanel, BorderLayout.CENTER);

        JPanel interactionPanel = new JPanel(new GridLayout(1, 0, 3, 0));
        interactionPanel.setBackground(AppThemeColor.MSG_HEADER);
        JButton inviteMeButton = componentsFactory.getIconButton(IconConst.CHAT_SCANNER_RESPONSE, 16, AppThemeColor.MSG_HEADER, TranslationKey.quick_response.value());
        inviteMeButton.addActionListener(e -> this.controller.performResponse(this.config.get().getResponseMessage()));
        JButton inviteButton = componentsFactory.getIconButton(IconConst.INVITE, 15, AppThemeColor.MSG_HEADER, TranslationKey.invite.value());
        inviteButton.addActionListener(e -> {
            this.controller.performInvite();
            root.setBorder(BorderFactory.createLineBorder(AppThemeColor.HEADER_SELECTED_BORDER));
        });
        JButton visiteHideout = componentsFactory.getIconButton(IconConst.VISIT_HIDEOUT, 16, AppThemeColor.MSG_HEADER, TranslationKey.visit_ho.value());
        visiteHideout.addActionListener(e -> this.controller.visitHideout());
        JButton tradeButton = componentsFactory.getIconButton(IconConst.TRADE, 15, AppThemeColor.MSG_HEADER, TranslationKey.offer_trade.value());
        tradeButton.addActionListener(e -> this.controller.performOfferTrade());
        JButton leaveButton = componentsFactory.getIconButton(IconConst.LEAVE, 16, AppThemeColor.MSG_HEADER, TranslationKey.leave.value());
        leaveButton.addActionListener(e -> {
            this.controller.performLeave();
            if (this.notificationConfig.get().isDismissAfterLeave()) {
                this.controller.performHide();
            }
        });
        JButton openChatButton = componentsFactory.getIconButton(IconConst.CHAT_OPEN, 15, AppThemeColor.MSG_HEADER, TranslationKey.open_chat.value());
        openChatButton.addActionListener(e -> this.controller.performOpenChat());
        JButton whoIsButton = componentsFactory.getIconButton(IconConst.WHO_IS, 15, AppThemeColor.MSG_HEADER, TranslationKey.who_is.value());
        whoIsButton.addActionListener(e -> controller.performWhoIs());
        JButton hideButton = componentsFactory.getIconButton(IconConst.CLOSE, 15, AppThemeColor.MSG_HEADER, TranslationKey.close.value());
        hideButton.addActionListener(action -> {
            this.controller.performHide();
        });
        interactionPanel.add(inviteMeButton);
        interactionPanel.add(inviteButton);
        interactionPanel.add(visiteHideout);
        interactionPanel.add(tradeButton);
        interactionPanel.add(leaveButton);
        interactionPanel.add(whoIsButton);
        interactionPanel.add(openChatButton);
        interactionPanel.add(hideButton);

        this.interactButtonMap.clear();
        this.interactButtonMap.put(HotKeyType.N_QUICK_RESPONSE, inviteMeButton);
        this.interactButtonMap.put(HotKeyType.N_VISITE_HIDEOUT, visiteHideout);
        this.interactButtonMap.put(HotKeyType.N_TRADE_PLAYER, tradeButton);
        this.interactButtonMap.put(HotKeyType.N_LEAVE, leaveButton);
        this.interactButtonMap.put(HotKeyType.N_WHO_IS, whoIsButton);
        this.interactButtonMap.put(HotKeyType.N_OPEN_CHAT, openChatButton);
        this.interactButtonMap.put(HotKeyType.N_CLOSE_NOTIFICATION, hideButton);

        JPanel opPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        JPanel timePanel = this.getTimePanel();
        timePanel.setPreferredSize(new Dimension(50, 26));
        opPanel.add(timePanel, BorderLayout.CENTER);
        opPanel.add(interactionPanel, BorderLayout.LINE_END);
        root.add(opPanel, BorderLayout.LINE_END);
        return root;
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    protected void updateHotKeyPool() {
        this.hotKeysPool.clear();
        this.interactButtonMap.forEach((type, button) -> {
            HotKeyPair hotKeyPair = this.hotKeysConfig.get()
                    .getScannerNHotKeysList()
                    .stream()
                    .filter(it -> it.getType().equals(type))
                    .findAny().orElse(null);
            if (!hotKeyPair.getDescriptor().getTitle().equals("...")) {
                this.hotKeysPool.put(hotKeyPair.getDescriptor(), button);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }
}
