package dev.discord_server.config.exception.custom;


import dev.discord_server.common.response.ErrorDefineCode;
import lombok.Getter;

@Getter
public class BasicCustomException500 extends RuntimeException {
    private ErrorDefineCode code;

    public BasicCustomException500(ErrorDefineCode code) {
        super(code.getMessage());
        this.code = code;

    }
}


