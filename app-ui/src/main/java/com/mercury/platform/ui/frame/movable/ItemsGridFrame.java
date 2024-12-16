package com.mercury.platform.ui.frame.movable;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.descriptor.StashTabDescriptor;
import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.fields.style.MercuryScrollBarUI;
import com.mercury.platform.ui.components.panel.HorizontalScrollContainer;
import com.mercury.platform.ui.components.panel.grid.*;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

import static java.awt.event.ItemEvent.DESELECTED;
import static java.awt.event.ItemEvent.SELECTED;

public class ItemsGridFrame extends AbstractMovableComponentFrame {
    private ItemsGridPanel itemsGridPanel;
    private HorizontalScrollContainer tabsContainer;
    private StashTabsContainer stashTabsContainer;

    public ItemsGridFrame() {
        super();
        componentsFactory.setScale(this.scaleConfig.get("itemcell"));
        stubComponentsFactory.setScale(this.scaleConfig.get("itemcell"));
        enableMouseOverBorder = false;
        processHideEffect = false;
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.itemsGridPanel = new ItemsGridPanel(componentsFactory);
        this.stashTabsContainer = new StashTabsContainer();
    }

    @Override
    public void onViewInit() {
        this.setBackground(AppThemeColor.TRANSPARENT);
        this.getRootPane().setBorder(null);
        this.add(itemsGridPanel, BorderLayout.CENTER);
        this.setPreferredSize(this.getMaximumSize());
        this.pack();
    }

    @Override
    public void subscribe() {
        MercuryStoreUI.showItemGridSubject.subscribe(message -> {
            if (this.applicationConfig.get().isItemsGridEnable()) {
                if (itemsGridPanel.getActiveTabsCount() == 0) {
                    this.setVisible(true);
                }
                this.itemsGridPanel.add(message, null);
                this.pack();
            }
        });
        MercuryStoreCore.removeNotificationSubject.subscribe(message -> {
            if (message instanceof ItemTradeNotificationDescriptor) {
                this.itemsGridPanel.remove((ItemTradeNotificationDescriptor) message);
                if (itemsGridPanel.getActiveTabsCount() == 0) {
                    this.setVisible(false);
                }
            }
        });
        MercuryStoreUI.closeGridItemSubject.subscribe(
                message -> itemsGridPanel.remove(message));
        MercuryStoreUI.dismissTabInfoPanelSubject.subscribe(tabInfoPanel -> {
            tabsContainer.remove(tabInfoPanel);
            stashTabsContainer.removeTab(tabInfoPanel.getStashTabDescriptor());
            this.repaint();
            this.pack();
        });
        MercuryStoreUI.itemCellStateSubject.subscribe(item -> {
            itemsGridPanel.changeTabType(item);
            this.pack();
        });
    }

    @Override
    protected void onLock() {
        super.onLock();
        if (itemsGridPanel.getActiveTabsCount() > 0) {
            this.setVisible(true);
        }
    }

    @Override
    protected JPanel getPanelForPINSettings() {
        JPanel panel = componentsFactory.getTransparentPanel(new BorderLayout());
        JPanel topPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        topPanel.setBackground(AppThemeColor.FRAME);
        JPanel headerPanel = componentsFactory.getTransparentPanel(new BorderLayout());

        JPanel defaultGridPanel = createGridPanel(12,12);
        JPanel quadGridPanel = createGridPanel(24,24);

//        JPanel folderDefaultGridPanel = createGridPanel(12,12);
//        JPanel folderQuadGridPanel = createGridPanel(24,24);
//
//        JPanel separator = componentsFactory.getSeparator();

        JPanel labelPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        JComboBox tabType = componentsFactory.getComboBox(new String[]{"1x1", "4x4"});//, "Folder 1x1", "Folder 4x4"});
        tabType.addItemListener(e -> {
            String item;
            switch (e.getStateChange()) {
                case DESELECTED:
                    item = (String) e.getItem();

                    switch (item) {
                        case "1x1":
                            panel.remove(defaultGridPanel);
                            break;
                        case "4x4":
                            panel.remove(quadGridPanel);
                            break;
//                        case "Folder 1x1":
//                            topPanel.remove(separator);
//                            panel.remove(folderDefaultGridPanel);
//                            break;
//                        case "Folder 4x4":
//                            topPanel.remove(separator);
//                            panel.remove(folderQuadGridPanel);
//                            break;
                    }
                    break;
                case SELECTED:
                    item = (String) e.getItem();

                    switch (item) {
                        case "1x1":
                            panel.add(defaultGridPanel, BorderLayout.CENTER);
                            break;
                        case "4x4":
                            panel.add(quadGridPanel, BorderLayout.CENTER);
                            break;
//                        case "Folder 1x1":
//                            topPanel.add(separator, BorderLayout.PAGE_END);
//                            panel.add(folderDefaultGridPanel, BorderLayout.CENTER);
//                            break;
//                        case "Folder 4x4":
//                            topPanel.add(separator, BorderLayout.PAGE_END);
//                            panel.add(folderQuadGridPanel, BorderLayout.CENTER);
//                            break;
                    }
                    this.pack();
                    this.repaint();
                    this.revalidate();
                    break;
            }
        });
        tabType.setPreferredSize(new Dimension((int) (componentsFactory.getScale() * 70), tabType.getHeight()));

        labelPanel.add(this.componentsFactory.wrapToSlide(tabType, AppThemeColor.FRAME), BorderLayout.LINE_START);
        Color titleColor = this.applicationConfig.get().isItemsGridEnable() ? AppThemeColor.TEXT_NICKNAME : AppThemeColor.TEXT_DISABLE;
        JLabel titleLabel = componentsFactory.getTextLabel(FontStyle.BOLD, titleColor, TextAlignment.LEFTOP, 20f, TranslationKey.align_this_grid.value());
        labelPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(labelPanel, BorderLayout.CENTER);

        String title = (this.applicationConfig.get().isItemsGridEnable()) ? TranslationKey.disable.value() : TranslationKey.enable.value();
        JButton disableButton = componentsFactory.getButton(title);
        componentsFactory.setUpToggleCallbacks(disableButton,
                () -> {
                    disableButton.setText(TranslationKey.enable.value());
                    titleLabel.setForeground(AppThemeColor.TEXT_DISABLE);
                    applicationConfig.get().setItemsGridEnable(false);
                    repaint();
                },
                () -> {
                    disableButton.setText(TranslationKey.disable.value());
                    titleLabel.setForeground(AppThemeColor.TEXT_NICKNAME);
                    applicationConfig.get().setItemsGridEnable(true);
                    repaint();
                }, this.applicationConfig.get().isItemsGridEnable());
        JButton hideButton = componentsFactory.getButton(TranslationKey.save.value());
        hideButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    stashTabsContainer.save();
                    MercuryStoreCore.saveConfigSubject.onNext(true);
                    FramesManager.INSTANCE.disableMovement(ItemsGridFrame.class);
                    repaint();
                }
            }
        });

        JButton dismissAllButton = componentsFactory.getButton(TranslationKey.dismiss_all.value());
        dismissAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    stashTabsContainer.removeAll();
                    stashTabsContainer.save();
                    MercuryStoreCore.saveConfigSubject.onNext(true);
                    tabsContainer.removeAll();
                    repaint();
                }
            }
        });

        JPanel disablePanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.RIGHT));
        disablePanel.add(disableButton);
        disablePanel.add(hideButton);
        disablePanel.add(dismissAllButton);

        JPanel savedTabsPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        savedTabsPanel.setPreferredSize(new Dimension(50, (int) (56 * componentsFactory.getScale())));
        tabsContainer = new HorizontalScrollContainer();
        tabsContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        tabsContainer.setBackground(AppThemeColor.TRANSPARENT);
        JScrollPane scrollPane = new JScrollPane(tabsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppThemeColor.FRAME);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                repaint();
            }
        });
        JScrollBar hBar = scrollPane.getHorizontalScrollBar();
        hBar.setBackground(AppThemeColor.SLIDE_BG);
        hBar.setUI(new MercuryScrollBarUI());
        hBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 16));
        hBar.setUnitIncrement(3);
        hBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        hBar.addAdjustmentListener(e -> repaint());

        savedTabsPanel.add(scrollPane, BorderLayout.CENTER);
        tabsContainer.getParent().setBackground(AppThemeColor.TRANSPARENT);
        stashTabsContainer.getStashTabDescriptors().forEach(stashTab -> {
            TabInfoPanel tabInfoPanel = new TabInfoPanel(stashTab, componentsFactory);
            tabsContainer.add(tabInfoPanel);
        });

        headerPanel.add(disablePanel, BorderLayout.LINE_END);
        topPanel.add(headerPanel, BorderLayout.PAGE_START);
        topPanel.add(savedTabsPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.PAGE_START);
        panel.add(defaultGridPanel, BorderLayout.CENTER);
        setUpResizePanels(panel);
        return panel;
    }

    private JPanel createGridPanel(int rows, int cols) {
        JPanel gridPanel = componentsFactory.getTransparentPanel(new GridLayout(rows, cols));
        gridPanel.setBorder(null);
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                gridPanel.add(getCellPlaceholder());
            }
        }
        gridPanel.setBackground(AppThemeColor.FRAME_ALPHA);
        return gridPanel;
    }

    private JPanel getCellPlaceholder() {
        JPanel cell = new JPanel();
        cell.setOpaque(true);
        cell.setBackground(AppThemeColor.TRANSPARENT);
        cell.setBorder(BorderFactory.createLineBorder(AppThemeColor.SCROLL_BAR));
        return cell;
    }

    private void setUpResizePanels(JPanel root) {
        JLabel rightArrow = componentsFactory.getIconLabel("app/default-mp.png", 16); //todo
        JPanel rightPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        rightPanel.setBackground(AppThemeColor.FRAME);
        rightPanel.add(rightArrow, BorderLayout.CENTER);

        rightPanel.addMouseListener(new ArrowMouseListener(rightPanel, new Cursor(Cursor.E_RESIZE_CURSOR)));
        rightPanel.addMouseMotionListener(new ResizeByWidthMouseMotionListener());
        JLabel downArrow = componentsFactory.getIconLabel("app/expand-mp.png", 16); //todo
        JPanel downPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        downPanel.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
        downPanel.setBackground(AppThemeColor.FRAME);
        downPanel.add(downArrow);

        downPanel.addMouseListener(new ArrowMouseListener(downPanel, new Cursor(Cursor.N_RESIZE_CURSOR)));
        downPanel.addMouseMotionListener(new ResizeByHeightMouseMotionListener());

        root.add(rightPanel, BorderLayout.LINE_END);
        root.add(downPanel, BorderLayout.PAGE_END);
    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }

    @Override
    protected void registerDirectScaleHandler() {
        MercuryStoreUI.itemPanelScaleSubject.subscribe(this::changeScale);
    }

    @Override
    protected JPanel defaultView(ComponentsFactory factory) {
        JPanel root = factory.getTransparentPanel(new BorderLayout());
        root.setSize(this.getSize());
        ItemsGridPanel defaultView = new ItemsGridPanel(factory);
        ItemTradeNotificationDescriptor message = new ItemTradeNotificationDescriptor();
        message.setWhisperNickname("Example1");
        message.setTabName("Example");
        message.setLeft(5);
        message.setTop(5);

        ItemInfoPanelController controller = new ItemInfoPanelController() {
            @Override
            public void hidePanel() {
            }

            @Override
            public void changeTabType(@NonNull ItemInfoPanel panel) {
            }
        };
        defaultView.add(message, controller);
        root.add(defaultView, BorderLayout.CENTER);
        return root;
    }

    @Override
    protected void performScaling(Map<String, Float> scaleData) {
        this.componentsFactory.setScale(scaleData.get("itemcell"));
    }

    private class ResizeByWidthMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            JPanel source = (JPanel) e.getSource();
            Point frameLocation = getLocation();
            setSize(new Dimension(e.getLocationOnScreen().x - frameLocation.x + source.getWidth(), getHeight()));
        }
    }

    private class ResizeByHeightMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            JPanel source = (JPanel) e.getSource();
            Point frameLocation = getLocation();
            setSize(new Dimension(getWidth(), e.getLocationOnScreen().y - frameLocation.y + source.getHeight()));
        }
    }

    private class ArrowMouseListener extends MouseAdapter {
        private JPanel panel;
        private Cursor cursor;

        private ArrowMouseListener(JPanel panel, Cursor cursor) {
            this.panel = panel;
            this.cursor = cursor;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Dimension size = framesConfig.getMinimumSize(ItemsGridFrame.this.getClass().getSimpleName());
            ItemsGridFrame.this.setMinimumSize(size);
            panel.setBackground(AppThemeColor.TEXT_DISABLE);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Dimension size = ItemsGridFrame.this.getSize();
            ItemsGridFrame.this.setMaximumSize(size);
            ItemsGridFrame.this.setMinimumSize(size);
            ItemsGridFrame.this.setPreferredSize(size);
            panel.setBackground(AppThemeColor.FRAME);
            framesConfig.get(ItemsGridFrame.class.getSimpleName()).setFrameSize(getSize());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(cursor);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
