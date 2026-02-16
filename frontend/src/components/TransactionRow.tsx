import { useState } from 'react';
import { CheckIcon } from '@heroicons/react/24/solid';
import { format } from 'date-fns';
import { CategoryPicker } from './CategoryPicker';
import { updateTransaction, confirmTransaction } from '@/services/api';
import type { Transaction } from '@/types';

interface TransactionRowProps {
  transaction: Transaction;
  categories: Array<{ id: string; name: string }>;
  onUpdate?: () => void;
  showConfirmButton?: boolean;
}

const SOURCE_COLORS: Record<string, string> = {
  simplefin: 'bg-blue-100 text-blue-800',
  manual: 'bg-slate-100 text-slate-700',
};

const PAYMENT_METHOD_LABELS: Record<string, string> = {
  card: 'Card',
  cash: 'Cash',
  transfer: 'Transfer',
};

export function TransactionRow({
  transaction,
  categories,
  onUpdate,
  showConfirmButton = true,
}: TransactionRowProps) {
  const [isUpdating, setIsUpdating] = useState(false);
  const [isConfirming, setIsConfirming] = useState(false);

  const handleCategoryChange = async (categoryId: string | null) => {
    if (categoryId === transaction.categoryId) return;
    setIsUpdating(true);
    try {
      await updateTransaction(transaction.id, { categoryId });
      onUpdate?.();
    } catch {
      // Error handling could show toast
    } finally {
      setIsUpdating(false);
    }
  };

  const handleConfirm = async () => {
    setIsConfirming(true);
    try {
      await confirmTransaction(transaction.id);
      onUpdate?.();
    } catch {
      // Error handling
    } finally {
      setIsConfirming(false);
    }
  };

  const isExpense = transaction.amount < 0;
  const sourceColor =
    SOURCE_COLORS[transaction.source] || 'bg-slate-100 text-slate-700';

  return (
    <div
      className={`rounded-lg border bg-white p-4 shadow-sm transition-shadow hover:shadow ${
        !transaction.isConfirmed ? 'border-amber-200 bg-amber-50/30' : 'border-slate-200'
      }`}
    >
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex-1 min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-sm font-medium text-slate-900">
              {transaction.description || transaction.merchant || 'No description'}
            </span>
            <span
              className={`rounded-full px-2 py-0.5 text-xs font-medium ${sourceColor}`}
            >
              {transaction.source}
            </span>
            {!transaction.isConfirmed && (
              <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-800">
                Unconfirmed
              </span>
            )}
          </div>
          <div className="mt-1 flex flex-wrap items-center gap-2 text-sm text-slate-500">
            <span>{format(new Date(transaction.date), 'MMM d, yyyy')}</span>
            <span>•</span>
            <span>{PAYMENT_METHOD_LABELS[transaction.paymentMethod] || transaction.paymentMethod}</span>
          </div>
        </div>
        <div className="flex items-center gap-3 sm:gap-4">
          <span
            className={`text-lg font-semibold ${
              isExpense ? 'text-red-600' : 'text-emerald-600'
            }`}
          >
            {isExpense ? '-' : '+'}
            {Math.abs(transaction.amount).toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}{' '}
            {transaction.currency}
          </span>
          {showConfirmButton && !transaction.isConfirmed && (
            <button
              type="button"
              onClick={handleConfirm}
              disabled={isConfirming}
              className="flex items-center gap-1 rounded-lg bg-primary-600 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:opacity-50"
            >
              <CheckIcon className="h-4 w-4" />
              Confirm
            </button>
          )}
        </div>
      </div>
      <div className="mt-3">
        <div className="w-full sm:w-64">
          <CategoryPicker
            value={transaction.categoryId}
            onChange={handleCategoryChange}
            suggestedCategoryId={transaction.suggestedCategoryId}
            disabled={isUpdating}
          />
        </div>
      </div>
    </div>
  );
}
