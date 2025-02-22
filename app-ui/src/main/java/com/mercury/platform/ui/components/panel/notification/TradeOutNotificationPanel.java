package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.config.descriptor.HotKeyPair;
import com.mercury.platform.shared.config.descriptor.HotKeyType;
import com.mercury.platform.shared.entity.message.TradeNotificationDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.notification.controller.OutgoingPanelController;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.TooltipConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Subscription;

import javax.swing.*;
import java.awt.*;


public abstract class TradeOutNotificationPanel<T extends TradeNotificationDescriptor> extends TradeNotificationPanel<T, OutgoingPanelController> {
    private Subscription autoCloseSubscription;
    private final static Logger logger = LogManager.getLogger(TradeOutNotificationPanel.class);

    @Override
    protected JPanel getHeader() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppThemeColor.MSG_HEADER);

        JPanel nickNamePanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        this.nicknameLabel = this.componentsFactory.getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_NICKNAME, TextAlignment.LEFTOP, 15f, this.data.getWhisperNickname());
        this.nicknameLabel.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
        JPanel headerPanel = this.componentsFactory.getJPanel(new GridBagLayout(), AppThemeColor.MSG_HEADER);
        headerPanel.add(this.getExpandButton());

        JPanel nickLabelPanel = this.getNicknamePanel(this.nicknameLabel);

        headerPanel.add(nickLabelPanel);
        headerPanel.add(this.getForPanel("app/outgoing_arrow.png"));
        nickNamePanel.add(headerPanel, BorderLayout.LINE_START);
        root.add(nickNamePanel, BorderLayout.CENTER);

        JPanel opPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.MSG_HEADER);
        JPanel interactionPanel = new JPanel(new GridLayout(1, 0, 3, 0));
        interactionPanel.setBackground(AppThemeColor.MSG_HEADER);
        JButton visiteHideout = componentsFactory.getIconButton(IconConst.VISIT_HIDEOUT, 16, AppThemeColor.MSG_HEADER, TranslationKey.visit_ho.value());
        visiteHideout.addActionListener(e -> this.controller.visitHideout());
        JButton tradeButton = componentsFactory.getIconButton(IconConst.TRADE, 15, AppThemeColor.MSG_HEADER, TranslationKey.offer_trade.value());
        tradeButton.addActionListener(e -> this.controller.performOfferTrade());
        JButton leaveButton = componentsFactory.getIconButton(IconConst.LEAVE, 16, AppThemeColor.MSG_HEADER, TranslationKey.leave.value());
        leaveButton.addActionListener(e -> {
            this.controller.performLeave(this.notificationConfig.get().getPlayerNickname());
            if (this.notificationConfig.get().isDismissAfterLeave()) {
                this.controller.performHide();
            }
        });
        JButton openChatButton = componentsFactory.getIconButton(IconConst.CHAT_OPEN, 15, AppThemeColor.MSG_HEADER, TranslationKey.open_chat.value());
        openChatButton.addActionListener(e -> controller.performOpenChat());
        JButton whoIsButton = componentsFactory.getIconButton(IconConst.WHO_IS, 15, AppThemeColor.MSG_HEADER, TranslationKey.who_is.value());
        whoIsButton.addActionListener(e -> controller.performWhoIs());
        JButton hideButton = componentsFactory.getIconButton(IconConst.CLOSE, 15, AppThemeColor.MSG_HEADER, TranslationKey.close.value());
        hideButton.addActionListener(action -> {
            this.controller.performHide();
        });
        interactionPanel.add(visiteHideout);
        interactionPanel.add(tradeButton);
        interactionPanel.add(leaveButton);
        interactionPanel.add(whoIsButton);
        interactionPanel.add(openChatButton);
        interactionPanel.add(hideButton);
        this.interactButtonMap.clear();
        this.interactButtonMap.put(HotKeyType.N_VISITE_HIDEOUT, visiteHideout);
        this.interactButtonMap.put(HotKeyType.N_TRADE_PLAYER, tradeButton);
        this.interactButtonMap.put(HotKeyType.N_LEAVE, leaveButton);
        this.interactButtonMap.put(HotKeyType.N_WHO_IS, whoIsButton);
        this.interactButtonMap.put(HotKeyType.N_OPEN_CHAT, openChatButton);
        this.interactButtonMap.put(HotKeyType.N_CLOSE_NOTIFICATION, hideButton);

        JPanel timePanel = this.getTimePanel();
        opPanel.add(timePanel, BorderLayout.CENTER);
        opPanel.add(interactionPanel, BorderLayout.LINE_END);

        root.add(opPanel, BorderLayout.LINE_END);
        return root;
    }

    @Override
    public void subscribe() {
        super.subscribe();
        this.autoCloseSubscription = MercuryStoreCore.plainMessageSubject.subscribe(message -> {
            if (this.data.getWhisperNickname().equals(message.getNickName())) {

                try {
                    if (this.notificationConfig.get()
                            .getAutoCloseTriggers().stream()
                            .anyMatch(it -> message.getMessage().toLowerCase()
                                    .contains(StringUtils.normalizeSpace(it.toLowerCase())))) {
                        this.controller.performHide();
                    }
                } catch (Exception e) {
                    logger.error("Failed on subscribing to notification on auto close subscribtion", e);
                }
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
        this.autoCloseSubscription.unsubscribe();
    }

    protected JButton getRepeatButton() {
        JButton repeatButton = componentsFactory.getIconButton(IconConst.RELOAD_HISTORY, 15, AppThemeColor.FRAME, TranslationKey.repeat_message.value());
        repeatButton.addActionListener(action -> {
            this.controller.performResponse(this.data.getSourceString());
            repeatButton.setEnabled(false);
            Timer timer = new Timer(5000, event -> {
                repeatButton.setEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();
        });
        return repeatButton;
    }

    @Override
    protected void updateHotKeyPool() {
        this.hotKeysPool.clear();
        this.interactButtonMap.forEach((type, button) -> {
            HotKeyPair hotKeyPair = this.hotKeysConfig.get()
                    .getOutNHotKeysList()
                    .stream()
                    .filter(it -> it.getType().equals(type))
                    .findAny().orElse(null);
            if (hotKeyPair != null && !hotKeyPair.getDescriptor().getTitle().equals("...")) {
                this.hotKeysPool.put(hotKeyPair.getDescriptor(), button);
            }
        });
        this.initResponseButtonsPanel(this.notificationConfig.get().getOutButtons(), true);
        Window windowAncestor = SwingUtilities.getWindowAncestor(TradeOutNotificationPanel.this);
        if (windowAncestor != null) {
            windowAncestor.pack();
        }
    }
}
