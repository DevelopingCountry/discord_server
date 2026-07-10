# Discord Server — CLAUDE.md

## 프로젝트 개요

Discord 클론 **백엔드** 서버. Spring Boot 기반으로 실시간 채팅, DM, 음성 통신, 친구 시스템, 서버(길드) 관리 기능을 제공한다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| 언어/런타임 | Java 17 |
| 프레임워크 | Spring Boot 3.2.4 |
| 빌드 | Gradle |
| DB | MySQL 8.0 + Spring Data JPA (Hibernate) |
| 캐시/메시징 | Redis (Pub/Sub + Refresh Token 저장) |
| 실시간 채팅 | WebSocket + STOMP (SockJS) |
| 실시간 음성 | raw WebSocket (WebRTC signaling) |
| 인증 | JWT (Access 3h / Refresh 7d) + Kakao OAuth 2.0 |
| ID 생성 | Snowflake ID (분산 환경 대응) |
| API 문서 | SpringDoc OpenAPI (Swagger UI) |
| 배포 | Docker + Docker Compose |

## 프로젝트 구조

```
src/main/java/dev/discord_server/
├── DiscordServerApplication.java   # 메인 진입점 (@EnableJpaAuditing)
├── auth/                           # JWT 필터, JwtUtil, Kakao OAuth, AuthController
├── config/
│   ├── SecurityConfig.java         # Spring Security + CORS + JWT 필터 설정
│   ├── WebSocketConfig.java        # STOMP /ws-chat 엔드포인트 설정
│   ├── VoiceSocketConfig.java      # Voice /ws/voice 엔드포인트 설정
│   ├── WebConfig.java              # CORS (http://localhost:3000)
│   ├── RestTemplateConfig.java     # Kakao OAuth용 RestTemplate
│   └── redis/
│       ├── RedisConfig.java        # Lettuce 연결 팩토리
│       └── RedisPubSubConfig.java  # Pub/Sub 토픽 + Subscriber 등록
└── domain/
    ├── user/          # 유저 프로필, 닉네임 변경
    ├── server/        # 서버(길드) CRUD, 초대, 알림, 탈퇴
    ├── channel/       # 채널 CRUD (ChannelType: CHAT/VOICE)
    ├── message/       # 채널 메시지 REST + WebSocket
    ├── dm/            # DM 대화 생성/조회
    ├── dm_message/    # DM 메시지 REST + WebSocket + Presence tracking
    ├── friend/        # 친구 요청/수락/거절, 온라인 상태
    ├── serverUser/    # 서버 멤버십 (alarm 설정 포함)
    └── nickname/      # 랜덤 닉네임 풀 관리
```

각 도메인 패키지는 `controller / service / entity / repository / dto` 구조를 따른다.

## 도메인 모델

| Entity | 핵심 필드 | Enum |
|--------|----------|------|
| `User` | id, nickname, email, password, imageUrl, role | `Role`: USER, ADMIN |
| `Server` | id, serverName, image, host(User) | — |
| `Channel` | id, name, type, serverId, creatorId | `ChannelType`: CHAT, VOICE |
| `Message` | id, content, channelId, userId | — |
| `Friend` | id, fromUserId, toUserId, status | `FriendStatus`: PENDING, ACCEPTED, REJECTED |
| `Dm` | id, user1Id, user2Id, isVisible | — |
| `DmMessage` | id, content, dmId, userId | — |
| `ServerUser` | id, serverId, userId, alarm | — |
| `ServerInvite` | id, serverId, fromUserId, toUserId, status | `InviteStatus`: PENDING, ACCEPTED, DECLINED |
| `Nickname` | id, nickname, isUsed | — |

모든 Entity는 `BaseEntity`를 상속해 `createdAt` / `updatedAt` 자동 기록.  
ID는 DB auto-increment 대신 **Snowflake ID** 사용 (`SnowflakeIdGenerator.java`, epoch 2021-01-01).

## REST API 엔드포인트

```
# 인증
GET  /auth/login/kakao          # Kakao OAuth 로그인
POST /auth/refresh              # Access Token 재발급

# 유저
GET  /me                        # 내 프로필 조회
PATCH /me                       # 닉네임 변경
GET  /me/token/{userId}         # JWT 발급 (개발용)

# 서버
GET    /server                  # 내 서버 목록
POST   /server                  # 서버 생성
PATCH  /server/{id}             # 서버 이름/이미지 수정
DELETE /server/{id}             # 서버 삭제 (호스트 전용)
POST   /server/{id}/invite      # 유저 초대
POST   /server/{inviteId}/accept # 초대 수락
PATCH  /server/{id}/alarm       # 알림 토글
DELETE /server/{id}/leave       # 서버 탈퇴

# 채널
GET    /server/{id}/channel     # 채널 목록
POST   /server/{id}/channel     # 채널 생성
PATCH  /server/{id}/channel     # 채널 수정
DELETE /server/{id}/channel     # 채널 삭제

# 채널 메시지
GET    /channel/{id}/messages           # 메시지 목록
PATCH  /channel/{id}/message/{msgId}    # 메시지 수정
DELETE /channel/{id}/message/{msgId}    # 메시지 삭제

# 친구
GET    /friend                  # 친구 목록
POST   /friend                  # 친구 신청
PATCH  /friend                  # 수락/거절
DELETE /friend                  # 친구 삭제
POST   /friend/search           # 닉네임으로 유저 검색
GET    /friend/online           # 온라인 친구 목록

# DM
GET  /dm                        # DM 목록
POST /dm                        # DM 시작 또는 조회
POST /dm/visible                # DM 숨김/표시 토글

# DM 메시지
GET    /dm/{dmId}                          # DM 메시지 목록
PATCH  /dm/{dmId}/message/{msgId}          # DM 메시지 수정
DELETE /dm/{dmId}/message/{msgId}          # DM 메시지 삭제
```

## WebSocket

### STOMP (`/ws-chat`, SockJS 지원)
| 목적지 | 설명 |
|--------|------|
| `/app/channel/{channelId}` | 채널 메시지 전송 |
| `/app/dm/{dmId}` | DM 메시지 전송 |
| `/app/dm/{dmId}/enter` | DM 입장 (presence) |
| `/app/dm/{dmId}/leave` | DM 퇴장 (presence) |

메시지 타입: `SEND`, `UPDATE`, `DELETE`

### Voice (`/ws/voice`, raw WebSocket)
WebRTC signaling용 별도 핸들러 (`VoiceSignalingHandler`). JWT 인증 적용.

### Redis Pub/Sub 토픽
| 토픽 | 용도 |
|------|------|
| `chat.dm` | DM 메시지 배포 |
| `channel.msg` | 채널 메시지 배포 |
| `channel.event.*` | 채널 이벤트 (pattern topic) |
| `notifications` | 알림 (Redis Stream) |

Refresh Token도 Redis에 저장 (TTL 7일).

## 인증 흐름

1. Kakao OAuth → `AuthController` → `AuthService`
2. Access Token (3h) + Refresh Token (7d) 발급
3. 이후 요청: `Authorization: Bearer <access_token>` 헤더
4. JWT 필터 (`JwtAuthenticationFilter`) → `SecurityContextHolder`에 유저 정보 저장
5. `SecurityUtil.getCurrentUserId()` 로 컨트롤러에서 유저 ID 추출

WebSocket 연결 시에도 JWT 핸드셰이크 인터셉터로 검증.

## 설정 파일

| 파일 | 환경 |
|------|------|
| `src/main/resources/application.yml` | 로컬 개발 (localhost) |
| `src/main/resources/application-prod.yml` | 프로덕션 (Docker 서비스명 사용) |
| `sql/schema.sql` | DB 스키마 |
| `sql/serverData.sql` | 테스트 데이터 |

## 로컬 개발 환경 실행

```bash
# Redis 실행
docker-compose up -d

# MySQL: localhost:3306/discord_server 직접 실행 필요 (docker-compose에 미포함)
# schema.sql로 스키마 생성 후 실행

# 앱 실행
./gradlew bootRun

# Swagger UI
http://localhost:8080/swagger-ui/index.html
```

## 프로덕션 배포

```bash
# 빌드
./gradlew build

# 환경변수 설정 후 Docker 실행
# 필수 환경변수:
#   DB_USERNAME, DB_PASSWORD
#   JWT_ACCESS_SECRET, JWT_REFRESH_SECRET
SPRING_PROFILES_ACTIVE=prod java -jar build/libs/*.jar
```

`docker-compose.yml`에는 Redis만 포함. MySQL은 별도 인프라 필요.  
프로덕션에서 MySQL 호스트명: `discord-mysql`, Redis 호스트명: `discord-redis`.

## 주요 유틸리티 클래스

| 클래스 | 위치 | 역할 |
|--------|------|------|
| `JwtUtil` | `auth/` | JWT 생성/파싱 |
| `SecurityUtil` | `config/` | SecurityContext에서 유저 ID 추출 |
| `SnowflakeIdGenerator` | `config/` | 분산 Snowflake ID 생성 |
| `RedisPublisher` | `config/redis/` | Redis 토픽 발행 |
| `RefreshTokenRepository` | `auth/` | Redis 기반 Refresh Token CRUD |

## 테스트

현재 테스트 커버리지는 최소 수준 (Spring Context 로드 테스트 + Redis 연결 테스트).  
단위 테스트는 거의 없고 통합 테스트 위주. 신규 기능 작성 시 서비스 레이어 단위 테스트 추가 권장.
