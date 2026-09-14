/** domains/monglife-discovery-domain-device DeviceEntity */
export interface Device {
  deviceId: string;
  deviceName: string;
  fcmToken?: string | null;
  accountId?: number | null;
  /** 연결 계정 요약 (응답에서 조인). 백엔드에는 아직 없는 필드 */
  accountEmail?: string | null;
  accountName?: string | null;
  /** 백엔드 DeviceEntity 에는 아직 없는 필드 */
  createdAt?: string;
}
