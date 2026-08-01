import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-6 text-center">
      <p className="text-8xl font-bold text-primary-200 dark:text-primary-900">404</p>
      <h1 className="mt-4 text-2xl font-bold">Page not found</h1>
      <p className="mt-2 text-slate-500">The page you&apos;re looking for doesn&apos;t exist.</p>
      <Link to="/dashboard" className="btn-primary mt-6">
        Go to Dashboard
      </Link>
    </div>
  );
}
