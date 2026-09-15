/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** common-api (context-path /api 포함). 상대 경로면 같은 출처 */
  readonly VITE_API_BASE_URL?: string;
  /** 게이트웨이 (/api/character, /api/user). 아직 화면에서 쓰지 않는다 */
  readonly VITE_GATEWAY_BASE_URL?: string;
  readonly VITE_ENABLE_MSW?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
