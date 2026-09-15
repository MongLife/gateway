import { createBrowserRouter } from 'react-router-dom';
import { ProtectedRoute } from '@/shared/auth/ProtectedRoute';
import { AdminLayout } from './layout/AdminLayout';
import { LoginPage } from '@/features/login/pages/LoginPage';
import { DashboardPage } from '@/features/dashboard/pages/DashboardPage';
import { AccountListPage } from '@/features/accounts/pages/AccountListPage';
import { AccountDetailPage } from '@/features/accounts/pages/AccountDetailPage';
import { DeviceListPage } from '@/features/devices/pages/DeviceListPage';
import { ActiveSessionsPage } from '@/features/sessions/pages/ActiveSessionsPage';
import { AppVersionListPage } from '@/features/app-versions/pages/AppVersionListPage';
import { NotificationPage } from '@/features/notifications/pages/NotificationPage';
import { ErrorReportListPage } from '@/features/error-reports/pages/ErrorReportListPage';
import { ErrorReportDetailPage } from '@/features/error-reports/pages/ErrorReportDetailPage';
import { NotFoundPage } from './NotFoundPage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AdminLayout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: 'accounts', element: <AccountListPage /> },
          { path: 'accounts/:accountId', element: <AccountDetailPage /> },
          { path: 'devices', element: <DeviceListPage /> },
          { path: 'sessions', element: <ActiveSessionsPage /> },
          { path: 'app-versions', element: <AppVersionListPage /> },
          { path: 'notifications', element: <NotificationPage /> },
          { path: 'error-reports', element: <ErrorReportListPage /> },
          { path: 'error-reports/:reportId', element: <ErrorReportDetailPage /> },
          { path: '*', element: <NotFoundPage /> },
        ],
      },
    ],
  },
]);
