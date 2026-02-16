import { useState, useEffect } from 'react';
import { CheckCircleIcon } from '@heroicons/react/24/solid';
import { TransactionRow } from '@/components/TransactionRow';
import {
  getUnconfirmedTransactions,
  confirmTransaction,
  updateTransaction,
  getCategories,
} from '@/services/api';
import type { Transaction, Category } from '@/types';
import { format, subMonths } from 'date-fns';

export function ReviewQueue() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [confirmingAll, setConfirmingAll] = useState(false);
  const [startDate, setStartDate] = useState(
    format(subMonths(new Date(), 1), 'yyyy-MM-dd')
  );
  const [endDate, setEndDate] = useState(format(new Date(), 'yyyy-MM-dd'));

  const fetchData = async () => {
    setLoading(true);
    try {
      const [txRes, catRes] = await Promise.all([
        getUnconfirmedTransactions({ startDate, endDate }),
        getCategories(),
      ]);
      setTransactions(txRes.data);
      setCategories(catRes.data);
    } catch {
      setTransactions([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [startDate, endDate]);

  const handleConfirmAllSuggested = async () => {
    const withSuggestion = transactions.filter(
      (t) => t.suggestedCategoryId && !t.categoryId
    );
    if (withSuggestion.length === 0) return;

    setConfirmingAll(true);
    try {
      for (const tx of withSuggestion) {
        await updateTransaction(tx.id, {
          categoryId: tx.suggestedCategoryId!,
        });
        await confirmTransaction(tx.id);
      }
      await fetchData();
    } catch {
      // Error handling
    } finally {
      setConfirmingAll(false);
    }
  };

  const withSuggested = transactions.filter(
    (t) => t.suggestedCategoryId && !t.categoryId
  ).length;
  const confirmedCount = transactions.length;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Review Queue</h1>

      {/* Progress & filters */}
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <div className="mb-4 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-medium text-slate-700">
              <span className="font-semibold text-primary-600">
                {confirmedCount}
              </span>{' '}
              unconfirmed transactions to review
            </p>
            {withSuggested > 0 && (
              <p className="mt-1 text-xs text-slate-500">
                {withSuggested} have suggested categories
              </p>
            )}
          </div>
          <div className="flex flex-wrap gap-2">
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="rounded-lg border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            />
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="rounded-lg border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            />
            {withSuggested > 0 && (
              <button
                type="button"
                onClick={handleConfirmAllSuggested}
                disabled={confirmingAll}
                className="flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:opacity-50"
              >
                <CheckCircleIcon className="h-5 w-5" />
                Confirm All Suggested
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Transaction list */}
      <div className="space-y-3">
        {loading ? (
          <div className="space-y-3">
            {[1, 2, 3, 4].map((i) => (
              <div
                key={i}
                className="h-28 animate-pulse rounded-lg bg-slate-200"
              />
            ))}
          </div>
        ) : transactions.length > 0 ? (
          transactions.map((tx) => (
            <TransactionRow
              key={tx.id}
              transaction={tx}
              categories={categories}
              onUpdate={fetchData}
              showConfirmButton
            />
          ))
        ) : (
          <div className="flex flex-col items-center justify-center rounded-xl border border-slate-200 bg-white py-20 text-slate-500">
            <CheckCircleIcon className="h-16 w-16 text-emerald-400" />
            <p className="mt-4 text-lg font-medium text-slate-700">
              All caught up!
            </p>
            <p className="mt-1 text-sm">
              No unconfirmed transactions to review
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
