export interface Transaction {
  id: string;
  date: string;
  transactedAt?: string;
  source: string;
  amount: number;
  currency: string;
  description: string;
  merchant: string;
  categoryId: string | null;
  suggestedCategoryId: string | null;
  isConfirmed: boolean;
  pending?: boolean;
  paymentMethod: string;
  accountId?: string;
  accountName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: string;
  name: string;
  parentId: string | null;
}

export interface DashboardData {
  totalSpend: number;
  byCategory: Array<{ categoryId: string; categoryName: string; total: number }>;
  byMonth: Array<{ month: string; total: number }>;
  byPaymentMethod: Array<{ method: string; total: number }>;
  recentTransactions: Transaction[];
}

export interface SyncLog {
  source: string;
  timestamp: string;
  status: string;
  transactionCount: number;
  errorMessage: string;
}

export interface Connection {
  id: string;
  provider: string;
  lastSyncedAt: string;
}
