package me.matthewedevelopment.atheriallib.discord;

import java.util.List;

public class DiscordEmbed {

    private final DeleteConfig deleteConfig;
    private final String title;
    private final String embedColor;
    private final List<String> description;
    private final String footer;

    private DiscordEmbed(Builder builder) {
        this.deleteConfig = builder.deleteConfig;
        this.title = builder.title;
        this.embedColor = builder.embedColor;
        this.description = builder.description;
        this.footer = builder.footer;
    }

    public static class DeleteConfig {
        private final boolean enabled;
        private final int seconds;

        public DeleteConfig(boolean enabled, int seconds) {
            this.enabled = enabled;
            this.seconds = seconds;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getSeconds() {
            return seconds;
        }
    }

    public static class Builder {
        private DeleteConfig deleteConfig;
        private String title;
        private String embedColor;
        private List<String> description;
        private String footer;

        public Builder setDeleteConfig(boolean enabled, int seconds) {
            this.deleteConfig = new DeleteConfig(enabled, seconds);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setEmbedColor(String embedColor) {
            this.embedColor = embedColor;
            return this;
        }

        public Builder setDescription(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        public DiscordEmbed build() {
            return new DiscordEmbed(this);
        }
    }

    public DeleteConfig getDeleteConfig() {
        return deleteConfig;
    }

    public String getTitle() {
        return title;
    }

    public String getEmbedColor() {
        return embedColor;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getFooter() {
        return footer;
    }
}
