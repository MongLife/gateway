# monglife-discovery-app-admin-web

MongLife 디스커버리 **관리자 웹**. Vite + React 18 + TypeScript + React Router + TanStack Query + Tailwind v4.

> Gradle 서브프로젝트가 **아니다.** `settings.gradle` 에 include 하지 않는다. include 하면 `apps/build.gradle` 의
> `subprojects` 가 스프링 부트 플러그인을 강제 적용하고 `copyPrivate` 가 `src/main/resources` 를 지우려 든다.
> 폴더만 `apps/` 아래에 두고 Node 로 독립 빌드한다. CI 워크플로도 아직 이 모듈을 모른다.

## 실행

```bash
nvm use            # .nvmrc → Node 24
npm install
npm run dev        # http://localhost:5173  (기본: MSW 목 API)
```

| 스크립트 | |
|---|---|
| `npm run dev` | 개발 서버 |
| `npm run build` | `tsc -b` + Vite 빌드 → `dist/` |
| `npm run typecheck` / `npm run lint` / `npm run format` | |

로그인은 **이메일 인증(6자리 코드)** 이다. 목에서는 `admin@monglife.cloud`(ADMIN 권한) 만 통과하고,
발송된 코드는 브라우저 콘솔에 `[mock] email code …` 로 찍힌다. `000000` 은 항상 통과한다.

### 실서버(common-api) 에 붙이기

`.env.example` 을 `.env` 로 복사하고:

```
VITE_ENABLE_MSW=false
VITE_API_BASE_URL=/api      # vite.config.ts 의 proxy 가 /api → localhost:8010 으로 넘긴다
```

`/admin/**` 엔드포인트가 아직 백엔드에 없으므로 지금은 `/admin/notification/mongs` 만 실제로 동작한다.
비밀값은 `.env` 에도 두지 않는다 — 저장소가 퍼블릭이다.

## 구조

```
src/
  main.tsx                MSW 부트(VITE_ENABLE_MSW) → <App/>
  App.tsx                 Providers + RouterProvider
  styles/
    tokens.css            ★ 디자인 토큰(CSS 변수). 클로드 디자인 싱크 시 이 값만 교체
    index.css             @import tailwindcss + @theme inline 으로 토큰을 유틸리티에 매핑
  app/
    router.tsx            라우트 트리
    nav.ts                사이드바 메뉴 (기능 정의 0~4 순서)
    providers.tsx         QueryClient, AuthProvider
    layout/               AdminLayout / Sidebar / Header
  shared/
    api/client.ts         fetch 래퍼 — Authorization 헤더, ResponseDto 언래핑, 401 → /login
    api/types.ts          ResponseDto / PageResponseDto (백엔드 monglife-core 와 1:1)
    auth/                 AuthProvider · useAuth · tokenStorage · ProtectedRoute
    ui/                   ★ 최소 프리미티브 — 디자인 싱크 시 교체 대상
    components/           DataTable · StatCard · ConfirmDialog · SearchInput
  features/<기능>/
    types.ts              도메인 타입 (엔티티 기준)
    api.ts                엔드포인트 경로 — 실 API 가 생기면 여기만 수정
    queries.ts            TanStack Query 훅
    pages/                화면
  mocks/
    data/db.ts            인메모리 fixture (결정적 난수, 새로고침 전까지 변경 유지)
    handlers/*.ts         MSW 핸들러 — 위 api.ts 계약을 그대로 구현
```

| 경로 | 기능 |
|---|---|
| `/` | 0. 통계 — 사용자 현황, 로그인 추이, 일일 가입자 |
| `/accounts`, `/accounts/:id` | 1.1 계정 관리 (상세: 기기·로그인 이력·토큰 탭), 1.3 기기 연결/해제 |
| `/devices` | 1.2 기기 관리 (상세 모달, 삭제) |
| `/sessions` | 2.1 로그인 사용자 현황, 2.2 토큰 상세·만료(로그아웃) |
| `/app-versions` | 3. 앱 버전 등록·강제 업데이트 ON/OFF |
| `/notifications` | 4. 알림 가능 기기 조회·푸시 전송 |
| `/error-reports`, `/error-reports/:id` | 사용자 오류 신고 목록·상세·답변(이메일) |

## 목 API 계약

핸들러가 구현한 경로. 백엔드에 `/admin/**` 을 만들 때 이 계약을 기준으로 한다.
응답은 전부 `ResponseDto { code, message, result }`, 목록은 `PageResponseDto` + `X-Total-Count` 헤더.
목록은 `sort=field,asc|desc`(Spring Pageable 스타일) 와 아래 필터 파라미터를 받는다.

```
GET    /admin/accounts?page&size&query&platform&role&status(ACTIVE|DELETED)&sort(accountId|createdAt)
GET    /admin/accounts/:id            /devices   /login-histories
PATCH  /admin/accounts/:id            { name?, role?, isDeleted? }
GET    /admin/devices?page&size&query&unmappedOnly&fcm(REGISTERED|NONE)&deviceName&sort(createdAt)   (accountEmail/accountName 조인)
PUT    /admin/devices/:deviceId/account   { accountId }      DELETE 동일 경로 → 해제
DELETE /admin/devices/:deviceId           기기 삭제 (토큰도 함께)
GET    /admin/sessions?page&size&deviceName&appPackageName&buildVersion&sort(createdAt)   (토큰 + 계정 요약)
GET    /admin/sessions/tokens?page&size&accountId
DELETE /admin/sessions/:refreshToken      DELETE /admin/sessions/accounts/:accountId
GET    /admin/app-versions
POST   /admin/app-versions                { appPackageName, buildVersion, mustUpdate }
PATCH  /admin/app-versions/:id            { mustUpdate }
DELETE /admin/app-versions/:id
GET    /admin/notification/devices?page&size&query&accountId&deviceName
POST   /admin/notification/mongs          { accountId, title, body }   ← 실존
GET    /admin/error-reports?page&size&query&status&deviceName&appPackageName&buildVersion&sort(reportId|createdAt)
GET    /admin/error-reports/:id
GET    /admin/meta/filters                { deviceNames[], appPackageNames[], buildVersions[] }  필터 셀렉트 옵션
POST   /admin/error-reports/:id/replies   { content }  → 신고자 이메일로 발송
GET    /admin/stats/users                 { todayJoined, weekJoined, totalAccounts, inactiveAccounts(30일 미로그인), activeSessions }
GET    /admin/stats/logins?days           GET /admin/stats/signups?days
POST   /public/admin/auth/email/code      { email } → { expiresIn, resendAfter }   403 NOT_ADMIN_ACCOUNT · 429 재발송 제한
POST   /public/admin/auth/email/verify    { email, code } → { accountId, accessToken, refreshToken }   400 INVALID_CODE · 410 EXPIRED_CODE
POST   /public/auth/logout
```

## 다음 단계 (이 모듈 밖)

- **백엔드 엔티티에 없는 필드를 화면이 쓴다.** 관리자 API 를 만들 때 같이 추가해야 한다:
  `Account.platform`(google/apple/kakao — 지금은 `socialAccountId` 접두사로 추정),
  `Device.createdAt`, 기기 응답의 `accountEmail/accountName`(계정 조인), **오류 신고 도메인 전체**(테이블·답변 메일 발송), `AppVersion.createdAt/updatedAt`(두 엔티티가 `BaseTimeEntity` 를 상속하지 않음),
  `stats.inactiveAccounts`(로그인 이력 기준 30일 미로그인 집계).

- **관리자 이메일 인증 백엔드.** 코드 발송(메일)·검증 엔드포인트와 ADMIN 권한 화이트리스트, 재발송 제한. 토큰 재발급(`/public/auth/reissue`)도 TODO.
- **common-api 에 `/admin/**` 컨트롤러.** `SecurityConfig` 가 이미 `/admin/**` → `ROLE ADMIN` 이다.
  목록 조회용 QueryDSL 페이징이 `domain-account` / `domain-device` 에 없다.
- **CORS 또는 동일 출처.** common-api 에 CORS 설정이 없다. product `edge` nginx 에서 같은 호스트로 프록시하면 CORS 가 필요 없다.
- **정적 호스팅.** `configs/deploy/product/edge` nginx 에 admin 호스트(또는 `/admin/` 서브패스) + `try_files … /index.html` SPA 폴백 + 인증서.
- **Node CI.** `.github/actions/ci/build-test` 는 Gradle 전용이라 별도 워크플로가 필요하다.
