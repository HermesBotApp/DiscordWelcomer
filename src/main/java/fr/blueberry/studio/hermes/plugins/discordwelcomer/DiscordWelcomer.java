package fr.blueberry.studio.hermes.plugins.discordwelcomer;

import org.simpleyaml.configuration.file.YamlFile;

import fr.blueberry.studio.hermes.api.bots.BotManager;
import fr.blueberry.studio.hermes.api.plugins.Plugin;
import fr.blueberry.studio.hermes.plugins.discordwelcomer.listeners.discord.GuildMemberJoinListener;

public class DiscordWelcomer extends Plugin {

    public static DiscordWelcomer INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        final BotManager botManager = getHermes().getBotManager();
        final YamlFile config = getConfiguration();

        getHermes().getBotManager().getJDAListenerManager().registerJDAListener(new GuildMemberJoinListener(botManager, config));
    }

    @Override
    public void onDisable() {
    }
    
}
