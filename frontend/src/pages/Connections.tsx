import { useState, useEffect, useCallback } from 'react';
import {
  BuildingLibraryIcon,
  ArrowPathIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  ArrowTopRightOnSquareIcon,
} from '@heroicons/react/24/outline';
import { getConnections, createSimpleFINConnection, triggerSync } from '@/services/api';
import type { Connection } from '@/types';

const SIMPLEFIN_CREATE_URL = 'https://bridge.simplefin.org/simplefin/create';

export function Connections() {
  const [connections, setConnections] = useState<Connection[]>([]);
  const [loading, setLoading] = useState(true);
  const [connecting, setConnecting] = useState(false);
  const [syncing, setSyncing] = useState(false);
  const [showSetupForm, setShowSetupForm] = useState(false);
  const [setupToken, setSetupToken] = useState('');
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fetchConnections = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getConnections();
      setConnections(res.data ?? []);
    } catch {
      setConnections([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchConnections();
  }, [fetchConnections]);

  const handleConnectBank = () => {
    setShowSetupForm(true);
    setMessage(null);
  };

  const handleSubmitToken = async () => {
    const token = setupToken.trim();
    if (!token) {
      setMessage({ type: 'error', text: 'Please paste your SimpleFIN setup token.' });
      return;
    }

    setConnecting(true);
    setMessage(null);
    try {
      await createSimpleFINConnection(token);
      setMessage({ type: 'success', text: 'Bank accounts connected via SimpleFIN Bridge.' });
      setShowSetupForm(false);
      setSetupToken('');
      fetchConnections();
    } catch {
      setMessage({ type: 'error', text: 'Failed to connect. The token may have already been used or is invalid.' });
    } finally {
      setConnecting(false);
    }
  };

  const handleSync = async () => {
    setSyncing(true);
    setMessage(null);
    try {
      await triggerSync();
      setMessage({ type: 'success', text: 'Sync triggered successfully.' });
      fetchConnections();
    } catch {
      setMessage({ type: 'error', text: 'Sync failed. Please try again.' });
    } finally {
      setSyncing(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Bank Connections</h1>
        <div className="flex gap-3">
          <button
            type="button"
            onClick={handleSync}
            disabled={syncing || connections.length === 0}
            className="flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <ArrowPathIcon className={`h-5 w-5 ${syncing ? 'animate-spin' : ''}`} />
            Sync Now
          </button>
          <button
            type="button"
            onClick={handleConnectBank}
            disabled={connecting}
            className="flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <BuildingLibraryIcon className="h-5 w-5" />
            {connecting ? 'Connecting...' : 'Connect Bank'}
          </button>
        </div>
      </div>

      {message && (
        <div
          className={`flex items-center gap-3 rounded-lg border p-4 ${
            message.type === 'success'
              ? 'border-green-200 bg-green-50 text-green-800'
              : 'border-red-200 bg-red-50 text-red-800'
          }`}
        >
          {message.type === 'success' ? (
            <CheckCircleIcon className="h-5 w-5 flex-shrink-0" />
          ) : (
            <ExclamationTriangleIcon className="h-5 w-5 flex-shrink-0" />
          )}
          <p className="text-sm">{message.text}</p>
        </div>
      )}

      {showSetupForm && (
        <div className="rounded-xl border border-primary-200 bg-primary-50/50 p-6 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-900">Connect via SimpleFIN Bridge</h2>
          <div className="mt-3 space-y-4">
            <div className="rounded-lg bg-white p-4 text-sm text-slate-600">
              <p className="font-medium text-slate-800">Steps:</p>
              <ol className="mt-2 list-inside list-decimal space-y-1.5">
                <li>
                  <a
                    href={SIMPLEFIN_CREATE_URL}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-1 font-medium text-primary-600 hover:text-primary-700 underline"
                  >
                    Open SimpleFIN Bridge
                    <ArrowTopRightOnSquareIcon className="h-3.5 w-3.5" />
                  </a>{' '}
                  and sign in or create an account.
                </li>
                <li>Connect your bank and generate a <strong>Setup Token</strong>.</li>
                <li>Copy the token and paste it below.</li>
              </ol>
            </div>
            <div>
              <label htmlFor="setup-token" className="block text-sm font-medium text-slate-700">
                Setup Token
              </label>
              <textarea
                id="setup-token"
                rows={3}
                value={setupToken}
                onChange={(e) => setSetupToken(e.target.value)}
                placeholder="Paste your SimpleFIN setup token here..."
                className="mt-1 block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm shadow-sm placeholder:text-slate-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              />
            </div>
            <div className="flex gap-3">
              <button
                type="button"
                onClick={handleSubmitToken}
                disabled={connecting || !setupToken.trim()}
                className="flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {connecting ? 'Connecting...' : 'Connect'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowSetupForm(false);
                  setSetupToken('');
                }}
                className="rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 px-4 py-3">
          <h2 className="text-sm font-semibold text-slate-700">Connected Accounts</h2>
        </div>
        {loading ? (
          <div className="space-y-2 p-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-14 animate-pulse rounded-lg bg-slate-100" />
            ))}
          </div>
        ) : connections.length > 0 ? (
          <ul className="divide-y divide-slate-100">
            {connections.map((conn) => (
              <li key={conn.id} className="flex items-center justify-between px-4 py-4">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100">
                    <BuildingLibraryIcon className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-900 capitalize">
                      {conn.provider}
                    </p>
                    <p className="text-xs text-slate-500">
                      ID: {conn.id}
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <span className="inline-flex items-center gap-1 rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700">
                    <span className="h-1.5 w-1.5 rounded-full bg-green-500" />
                    Connected
                  </span>
                  {conn.lastSyncedAt && (
                    <p className="mt-1 text-xs text-slate-500">
                      Last synced: {new Date(conn.lastSyncedAt).toLocaleString()}
                    </p>
                  )}
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <div className="py-12 text-center">
            <BuildingLibraryIcon className="mx-auto h-12 w-12 text-slate-300" />
            <p className="mt-4 text-sm font-medium text-slate-900">No bank accounts connected</p>
            <p className="mt-1 text-sm text-slate-500">
              Connect your bank account via SimpleFIN Bridge to automatically import transactions.
            </p>
            <button
              type="button"
              onClick={handleConnectBank}
              disabled={connecting}
              className="mt-4 inline-flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <BuildingLibraryIcon className="h-5 w-5" />
              {connecting ? 'Connecting...' : 'Connect Bank'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
