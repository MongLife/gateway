import { authHandlers } from './auth';
import { accountHandlers } from './accounts';
import { deviceHandlers } from './devices';
import { sessionHandlers } from './sessions';
import { appVersionHandlers } from './appVersions';
import { notificationHandlers } from './notifications';
import { statsHandlers } from './stats';
import { errorReportHandlers } from './errorReports';
import { metaHandlers } from './meta';

export const handlers = [
  ...authHandlers,
  ...accountHandlers,
  ...deviceHandlers,
  ...sessionHandlers,
  ...appVersionHandlers,
  ...notificationHandlers,
  ...statsHandlers,
  ...errorReportHandlers,
  ...metaHandlers,
];
