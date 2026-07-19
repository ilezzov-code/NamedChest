package ru.ilezzov.namedchest.logger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConsoleMessage {
    private final ResourceBundle resourceBundle;

    public ConsoleMessage(final String file) {
        this.resourceBundle = ResourceBundle.getBundle(file);
    }

    public String getMessage(final String key) {
        if (this.resourceBundle == null) {
            return null;
        }

        return this.resourceBundle.getString(key);
    }

    public String getMessage(final String key, final Object... args) {
        if (this.resourceBundle == null) {
            return null;
        }

        final String pattern = this.resourceBundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
}
