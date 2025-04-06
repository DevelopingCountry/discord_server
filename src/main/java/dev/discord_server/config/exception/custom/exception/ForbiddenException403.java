package dev.discord_server.config.exception.custom.exception;


import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.BasicCustomException500;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 403 : 접근 권한이 부족할 때
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException403 extends BasicCustomException500 {
    public ForbiddenException403(ErrorDefineCode code) {
        super(code);
    }
}
