package dev.discord_server.domain.channel.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.channel.entity.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelMessageService {
    private final ChannelRepository channelRepository;

    public void sendMessage(Long channelId, Long senderId, String content){
        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        if(!channel.isMember(senderId))
    }
}
