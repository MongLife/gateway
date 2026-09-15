import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="py-20 text-center">
      <p className="text-4xl font-semibold text-primary">404</p>
      <p className="mt-2 text-sm text-muted-foreground">페이지를 찾을 수 없습니다.</p>
      <Link to="/" className="mt-4 inline-block text-sm text-primary underline">
        대시보드로
      </Link>
    </div>
  );
}
