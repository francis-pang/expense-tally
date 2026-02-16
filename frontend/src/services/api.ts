import axios, { AxiosInstance } from 'axios';
import { fetchAuthSession } from 'aws-amplify/auth';
import type {
  Category,
  Transaction,
  DashboardData,
  SyncLog,
  Connection,
} from '@/types';

const baseURL = import.meta.env.VITE_API_URL || '/api';

const api: AxiosInstance = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  async (config) => {
    try {
      const session = await fetchAuthSession();
      const token = session.tokens?.idToken?.toString();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch {
      // No session, continue without token
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Health
export const getHealth = () => api.get('/health');

// Categories
export const getCategories = () => api.get<Category[]>('/categories');
export const createCategory = (data: { name: string; parentId?: string }) =>
  api.post<Category>('/categories', data);
export const updateCategory = (
  id: string,
  data: { name?: string; parentId?: string }
) => api.put<Category>(`/categories/${id}`, data);
export const deleteCategory = (id: string) =>
  api.delete(`/categories/${id}`);

// Transactions
export const getTransactions = (params?: {
  year?: number;
  startDate?: string;
  endDate?: string;
  categoryId?: string;
  confirmed?: boolean;
}) => api.get<Transaction[]>('/transactions', { params });
export const createTransaction = (data: {
  date: string;
  amount: number;
  currency: string;
  description: string;
  merchant?: string;
  categoryId?: string;
  paymentMethod: string;
}) => api.post<Transaction>('/transactions', data);
export const updateTransaction = (
  id: string,
  data: {
    categoryId?: string;
    description?: string;
    merchant?: string;
    paymentMethod?: string;
  }
) => api.put<Transaction>(`/transactions/${id}`, data);
export const deleteTransaction = (id: string) =>
  api.delete(`/transactions/${id}`);
export const getUnconfirmedTransactions = (params?: {
  startDate?: string;
  endDate?: string;
}) => api.get<Transaction[]>('/transactions/unconfirmed', { params });
export const confirmTransaction = (id: string) =>
  api.put(`/transactions/${id}/confirm`);

// Dashboard
export const getDashboard = (params?: { year?: number; month?: number }) =>
  api.get<DashboardData>('/dashboard', { params });

// Sync
export const triggerSync = () => api.post('/sync/trigger');
export const getSyncLogs = () => api.get<SyncLog[]>('/sync/logs');

// Connections
export const getConnections = () => api.get<Connection[]>('/connections');
export const createTellerConnection = (data: {
  accountId: string;
  accessToken: string;
}) => api.post('/connections/teller', data);
