import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';

export function Login() {
  const { signIn, isLoading, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      navigate('/', { replace: true });
    }
  }, [isLoading, isAuthenticated, navigate]);

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-slate-50 via-white to-primary-50 px-4">
      <div className="w-full max-w-sm space-y-8 text-center">
        <div>
          <h1 className="text-3xl font-bold text-primary-600">
            Expense Tally
          </h1>
          <p className="mt-2 text-slate-600">
            Track your spending. Stay on budget.
          </p>
        </div>

        <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-lg">
          <p className="mb-6 text-sm text-slate-600">
            Sign in to access your expense dashboard and manage your finances.
          </p>
          <button
            type="button"
            onClick={signIn}
            disabled={isLoading}
            className="w-full rounded-lg bg-primary-600 py-3 font-medium text-white transition-colors hover:bg-primary-700 disabled:opacity-50"
          >
            {isLoading ? 'Loading...' : 'Sign in'}
          </button>
        </div>

        <p className="text-xs text-slate-500">
          You will be redirected to sign in securely.
        </p>
      </div>
    </div>
  );
}
