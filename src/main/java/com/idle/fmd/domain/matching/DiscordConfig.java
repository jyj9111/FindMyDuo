package com.idle.fmd.domain.matching;

import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class DiscordConfig {
    // Discord Api와 연결 되는 객체
    private JDA jda;
    // Discord 서버 id
    @Value("${discord.guild.id}")
    private String guildId;
    // Discord 서버에 있는 채널 id
    @Value("${discord.guild.category.voice.id}")
    private String voiceId;

    public DiscordConfig(@Value("${discord.key}") String botToken) {
        try {
            this.jda = JDABuilder.createDefault(botToken)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing("채널 생성 대기"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build();
        } catch (Exception error) {
            log.error(error.getMessage());
        }
        log.debug("Discord 서버 연결 성공");
    }

    public String createVoiceChannel(String channelName, int limit) {
        Guild guild = this.jda.getGuildById(this.guildId);
        String url = "";
        try {
            url =  this.getUrl(channelName, guild, this.voiceId, limit);
        } catch (RuntimeException error) {
            error.printStackTrace();
        }
        return url;
    }

    private String getUrl(String channelName, Guild guild, String categoryId, int limit) {
        Category category = guild.getCategoryById(categoryId);
        String channelUrl = "";
        try {
            VoiceChannel voiceChannel = category.createVoiceChannel(channelName)
                    .addPermissionOverride(
                            guild.getPublicRole(),
                            EnumSet.of(Permission.VOICE_CONNECT),
                            EnumSet.of(Permission.VIEW_CHANNEL)
                    )
                    .reason("음성 채널 생성").submit().get();
            voiceChannel.getManager().setUserLimit(limit).queue();
            channelUrl = voiceChannel.createInvite().setMaxAge(300).submit().get().getUrl();
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
            throw new BusinessException(BusinessExceptionCode.CANNOT_CREATE_VOICE_CHANNEL);
        }
        return channelUrl;
    }

    public void deleteVoiceChannel() {
        Guild guild = this.jda.getGuildById(this.guildId);
        List<VoiceChannel> channelList = guild.getVoiceChannels();
        for (VoiceChannel voiceChannel : channelList) {
            List<Member> memberList = voiceChannel.getMembers();
            if (memberList.isEmpty()) {
                voiceChannel.delete().reason("사용자가 없으므로 채널 삭제").queue();
            }
        }
    }
}
