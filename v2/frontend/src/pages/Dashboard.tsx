import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { ArrowPathIcon } from '@heroicons/react/24/outline';
import { SpendChart } from '@/components/SpendChart';
import { TransactionRow } from '@/components/TransactionRow';
import { getDashboard, getCategories } from '@/services/api';
import type { DashboardData, Category } from '@/types';

export function Dashboard() {
  const [data, setData] = useState<DashboardData | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState<number | ''>('');

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      try {
        const [dashboardRes, categoriesRes] = await Promise.all([
          getDashboard({ year, month: month || undefined }),
          getCategories(),
        ]);
        setData(dashboardRes.data);
        setCategories(categoriesRes.data);
      } catch {
        setData(null);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [year, month]);

  if (loading && !data) {
    return (
      <div className="space-y-6">
        <div className="h-8 w-48 animate-pulse rounded bg-slate-200" />
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-40 animate-pulse rounded-lg bg-slate-200" />
          ))}
        </div>
      </div>
    );
  }

  const categoryChartData =
    data?.byCategory.map((c) => ({ name: c.categoryName, value: c.total })) ??
    [];
  const monthChartData =
    data?.byMonth.map((m) => ({ name: m.month, value: m.total })) ?? [];
  const paymentChartData =
    data?.byPaymentMethod.map((p) => ({ name: p.method, value: p.total })) ?? [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
        <div className="flex gap-2">
          <select
            value={year}
            onChange={(e) => setYear(Number(e.target.value))}
            className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
          >
            {Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i).map(
              (y) => (
                <option key={y} value={y}>
                  {y}
                </option>
              )
            )}
          </select>
          <select
            value={month}
            onChange={(e) =>
              setMonth(e.target.value === '' ? '' : Number(e.target.value))
            }
            className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
          >
            <option value="">All months</option>
            {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
              <option key={m} value={m}>
                {format(new Date(2000, m - 1), 'MMMM')}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Total spend card */}
      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <p className="text-sm font-medium text-slate-500">Total Spend</p>
        <p className="mt-1 text-4xl font-bold text-primary-600">
          {data?.totalSpend?.toLocaleString('en-US', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          }) ?? '0.00'}{' '}
          <span className="text-2xl font-normal text-slate-500">USD</span>
        </p>
      </div>

      {/* Charts grid */}
      <div className="grid gap-6 lg:grid-cols-2">
        <SpendChart
          data={categoryChartData}
          type="pie"
          title="Spend by Category"
        />
        <SpendChart
          data={monthChartData}
          type="bar"
          title="Monthly Trend"
        />
      </div>
      <div className="lg:w-1/2">
        <SpendChart
          data={paymentChartData}
          type="donut"
          title="Payment Method"
        />
      </div>

      {/* Recent transactions */}
      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold text-slate-900">
          Recent Transactions
        </h2>
        {data?.recentTransactions?.length ? (
          <div className="space-y-3">
            {data.recentTransactions.map((tx) => (
              <TransactionRow
                key={tx.id}
                transaction={tx}
                categories={categories}
                onUpdate={() => {
                  getDashboard({ year, month: month || undefined }).then(
                    (res) => setData(res.data)
                  );
                }}
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-12 text-slate-500">
            <ArrowPathIcon className="h-12 w-12 text-slate-300" />
            <p className="mt-2 text-sm">No recent transactions</p>
            <p className="text-xs">Add expenses or sync your accounts to get started</p>
          </div>
        )}
      </div>
    </div>
  );
}
