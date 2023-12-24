package me.matthewedevelopment.atheriallib.message.title;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Matthew E on 5/24/2019 at 3:01 PM for the project atherialapi
 */
public class AtherialTitle implements Title {
    private String title;
    private String subTitle;
    private int stay;
    private int fadeIn;
    private int fadeOut;
    private static AtherialLib atherialPlugin;

    public AtherialTitle(String title, String subTitle, int stay, int fadeIn, int fadeOut) {
        this.title = title;
        this.subTitle = subTitle;
        this.stay = stay;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    private AtherialTitle(Builder builder) {
        title = builder.title;
        subTitle = builder.subTitle;
        stay = builder.stay;
        fadeIn = builder.fadeIn;
        fadeOut = builder.fadeOut;
    }

    public static void setAtherialPlugin(AtherialLib atherialPlugin) {
        AtherialTitle.atherialPlugin = atherialPlugin;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void send(Player... players) {
        for (Player player : players) {
            send(player);
        }
    }

    public static AtherialLib getAtherialPlugin() {
        return atherialPlugin;
    }

    public void send(List<Player> players) {
        for (Player player : players) {
            send(player);
        }
    }

    public static Builder builder(AtherialTitle copy) {
        Builder builder = new Builder();
        builder.title = copy.getTitle();
        builder.subTitle = copy.getSubTitle();
        builder.stay = copy.getStay();
        builder.fadeIn = copy.getFadeIn();
        builder.fadeOut = copy.getFadeOut();
        return builder;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public int getStay() {
        return stay;
    }

    @Override
    public int getFadeIn() {
        return fadeIn;
    }

    @Override
    public int getFadeOut() {
        return fadeOut;
    }


    public void send(Player player) {
        if (atherialPlugin == null) {
            System.err.println("Could not find atherial plugin instance for AtherialTitle");
            return;
        }
        atherialPlugin.getVersionProvider().sendTitle(player, title, subTitle, stay, fadeIn, fadeOut);

    }

    /**
     * {@code AtherialTitle} builder static inner class.
     */
    public static final class Builder {
        private String title;
        private String subTitle;
        private int stay;
        private int fadeIn;
        private int fadeOut;

        private Builder() {
        }

        /**
         * Sets the {@code title} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param title the {@code title} to set
         * @return a reference to this Builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the {@code subTitle} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param subTitle the {@code subTitle} to set
         * @return a reference to this Builder
         */
        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public void send(Player... players){
            this.build().send(players);
        }

        public void send(List<Player> players){
            this.build().send(players);
        }

        public void send(Player player){
            this.build().send(player);
        }
        /**
         * Sets the {@code stay} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param stay the {@code stay} to set
         * @return a reference to this Builder
         */
        public Builder stay(int stay) {
            this.stay = stay;
            return this;
        }

        /**
         * Sets the {@code fadeIn} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fadeIn the {@code fadeIn} to set
         * @return a reference to this Builder
         */
        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        /**
         * Sets the {@code fadeOut} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fadeOut the {@code fadeOut} to set
         * @return a reference to this Builder
         */
        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        /**
         * Returns a {@code AtherialTitle} built from the parameters previously set.
         *
         * @return a {@code AtherialTitle} built with parameters of this {@code AtherialTitle.Builder}
         */
        public AtherialTitle build() {
            return new AtherialTitle(this);
        }
    }
}
