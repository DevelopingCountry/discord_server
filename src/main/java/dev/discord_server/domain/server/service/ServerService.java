package dev.discord_server.domain.server.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.server.dto.ServerResponse;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 예외처리 사용 방법
 * config -> custom -> exception에 많이 쓰이는 에러 종류들 작성해놓음 ex) noSuchElementFoundException404
 * ErrorDefindCode에 본인이 사용하고 싶은 에러 작성하고 사용
 */


@RequiredArgsConstructor
@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public List<ServerResponse> findServers() {
        List<Server> all = serverRepository.findAll();
        if(all.isEmpty()) {
            throw new NoSuchElementFoundException404(ErrorDefineCode.EXAMPLE_OCCURE_ERROR);
        }

        List<ServerResponse> responseDtoStream = all.stream()
                .map((ServerResponse::toResponseDto))
                .toList();
        return responseDtoStream;
    }




}
