package fr.blueberry.studio.hermes.plugins.discordwelcomer.listeners.discord;

import org.simpleyaml.configuration.file.YamlFile;

import emoji4j.EmojiUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.blueberry.studio.hermes.api.bots.Bot;
import fr.blueberry.studio.hermes.api.bots.BotManager;
import fr.blueberry.studio.hermes.api.utils.MessageEmbedHelper;
import fr.blueberry.studio.hermes.api.utils.RandomHelper;
import fr.blueberry.studio.hermes.api.utils.ColorHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoinListener extends ListenerAdapter {
    private final BotManager botManager;
    private final YamlFile config;

    public GuildMemberJoinListener(BotManager botManager, YamlFile config) {
        this.botManager = botManager;
        this.config = config;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        service.schedule(() -> {
            final Bot bot = botManager.pickRandomBot(true);
            final TextChannel channel = event.getGuild().getTextChannelById(config.getLong("channel"));
            final Color color = ColorHelper.toRGB(config.getString("color"));
            final ArrayList<String> messages = (ArrayList<String>)config.getStringList("messages");
            final int randomIndex = RandomHelper.getRandomIndex(messages);
            final String welcome = messages.get(randomIndex).replaceAll("%mention%", event.getMember().getAsMention());
            final MessageEmbed embed = MessageEmbedHelper.getBuilder()
                .setDescription(welcome)
                .setThumbnail(config.getString("image").isBlank() ? null : config.getString("image"))
                .setTitle(config.getString("title"))
                .setColor(color)
                .build();

            bot.getJDA().getTextChannelById(channel.getId()).sendMessage(embed).queue(message -> {
                final String emote = config.getString("emote");

                if(EmojiUtils.isEmoji(emote)) {
                    message.addReaction(EmojiUtils.getEmoji(emote).getEmoji()).queue();
                } else {
                    message.addReaction(emote).queue();
                }
            });
            
        }, 1, TimeUnit.SECONDS);
    }
}
