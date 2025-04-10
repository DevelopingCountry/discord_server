package dev.discord_server.common.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDefineCode {

    UNCAUGHT("ERR_00", "Uncaught Exception"),
    VALID_ERROR("ERR_01", "Field Validation fail"),
    EXAMPLE_OCCURE_ERROR("ERR_02", "예제 코드에서 그냥 발생시킨 오류랍니다"),
    DUPLICATE_EXAMPLE_NAME("ERR_03", "Example로 중복된 이름을 사용할 수 없습니다"),
    AUTH_NOT_FOUND_EMAIL("ERR_04", "해당 이메일을 찾을 수 없습니다."),
    AUTHORIZATION_FAIL("ERR_05", "해당 권한이 없습니다."),
    AUTHENTICATE_FAIL("ERR_06", "권한 인증에 실패했습니다."),
    SELF_FRIEND_REQUEST("FRI_01","자신에게 친구 신청 할 수 없습니다."),
    DUPLICATE_FRIEND("FRI_02","이미 친구 요청이 존재합니다."),
    EMPTY_FRIEND("FRI_03", "존재하지 않는 친구 입니다."),
    NOT_VALID_FRIEND("FRI_04","친구 관계가 아닙니다."),
    NOT_VALID_FRIEND_STATUS("FRI_05","존재하지 않는 상태 코드 값 입니다."),
    AUTH_NOT_CHANGE_FRIEND_STATUS("FRI_06","수락/거절할 수 있는 친구 요청이 존재하지 않습니다."),
    NOT_DELETABLE_FRIEND_STATUS("FRI_07","수락된 친구 관계가 아닙니다."),
    EMPTY_USER("user_01","존재하지 않는 유저입니다.")
    ;

    private final String code;
    private final String message;

}
