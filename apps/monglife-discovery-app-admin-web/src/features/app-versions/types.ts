/** domains/monglife-discovery-domain-device AppVersionEntity */
export interface AppVersion {
  appVersionId: number;
  appPackageName: string;
  buildVersion: string;
  mustUpdate: boolean;
  /** 백엔드 AppVersionEntity 에는 아직 없는 필드 */
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateAppVersion {
  appPackageName: string;
  buildVersion: string;
  mustUpdate: boolean;
}
