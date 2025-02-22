package com.mercury.platform.core.utils.interceptor;

import com.mercury.platform.core.utils.interceptor.filter.MessageMatcher;
import com.mercury.platform.shared.messageparser.MessageParser;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.NotificationSettingsDescriptor;
import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Intercepts outgoing trade messages,
 * starting with {@code "@To"}, {@code "@Кому"} etc. depending og the game client language.
 */
public class TradeOutMessagesInterceptor extends MessageInterceptor {
    private MessageParser messageParser = new MessageParser();
    private PlainConfigurationService<NotificationSettingsDescriptor> config;
    private List<LocalizationMatcher> clients = new ArrayList<>();

    public TradeOutMessagesInterceptor() {
        this.config = Configuration.get().notificationConfiguration();
        this.clients.add(new EngOutLocalizationMatcher());
        this.clients.add(new RuOutLocalizationMatcher());
        this.clients.add(new ArabicOutLocalizationMatcher());
        this.clients.add(new BZOutLocalizationMatcher());
        this.clients.add(new FrenchOutLocalizationMatcher());
        this.clients.add(new GermanOutLocalizationMatcher());
    }

    @Override
    protected void process(String message) {
        if (this.config.get().isOutNotificationEnable()) {
            LocalizationMatcher localizationMatcher = this.clients.stream()
                    .filter(matcher -> matcher.isSuitableFor(message))
                    .findAny().orElse(null);
            if (localizationMatcher != null) {
                localizationMatcher.processMessage(message);
            }
        }
    }

    @Override
    protected MessageMatcher match() {
        return message ->
                this.clients.stream()
                        .filter(matcher -> matcher.isSuitableFor(message))
                        .findAny().orElse(null) != null;
    }

    private abstract class LocalizationMatcher {
        public boolean isSuitableFor(String message) {
            return message.contains("Hi, I would like") ||
                    message.contains("Hi, I'd like") ||
                    message.contains("I'd like") ||
                    message.contains("你好，我想購買") ||
                    message.contains("こんにちは") ||
                    message.contains("구매하고 싶습니다" /* "I would like to buy"*/) ||
                    message.contains("Здравствуйте, хочу купить у вас") /* "Hello, I would like to buy" */ ||
                    (message.contains("wtb") && message.contains("(stash")) ||
                    message.contains("안녕하세요, 강탈") ||
                    message.contains("Bonjour, je souhaiterais t'acheter") ||
                    message.contains("안녕하세요") || //pathofexile.com/trade/
                    message.contains("ich möchte") ||
                    message.contains("Olá, eu gostaria") ||
                    message.contains("Hola, quisiera comprar") ||
                    message.contains("สวัสดี เราต้องการซื้อ");
        }

        public abstract String trimString(String src);

        public NotificationDescriptor getDescriptor(String message) {
            return messageParser.parse(this.trimString(message));
        }

        public void processMessage(String message) {
            NotificationDescriptor notificationDescriptor = this.getDescriptor(message);
            if (notificationDescriptor != null) {
                MercuryStoreCore.newNotificationSubject.onNext(notificationDescriptor);
            }
        }
    }

    private class EngOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@To") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@To");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor == null) {
                return null;
            }
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }

    private class RuOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@Кому") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@Кому");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }

    private class ArabicOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@ถึง") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@ถึง");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }

    private class BZOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@Para") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@Para");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }

    private class FrenchOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@À") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@À");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }

    private class GermanOutLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@An") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@An");
        }

        @Override
        public NotificationDescriptor getDescriptor(String message) {
            NotificationDescriptor descriptor = messageParser.parse(this.trimString(message));
            if (descriptor instanceof ItemTradeNotificationDescriptor) {
                descriptor.setType(NotificationType.OUT_ITEM_MESSAGE);
            } else {
                descriptor.setType(NotificationType.OUT_CURRENCY_MESSAGE);
            }
            return descriptor;
        }
    }
}
