import { useState, useEffect, useCallback } from 'react';
import {
  getCurrentUser,
  fetchAuthSession,
  signInWithRedirect,
  signOut as amplifySignOut,
} from 'aws-amplify/auth';

export function useAuth() {
  const [user, setUser] = useState<{ username: string } | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const checkAuth = useCallback(async () => {
    try {
      const currentUser = await getCurrentUser();
      setUser({ username: currentUser.username });
    } catch {
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const signIn = useCallback(async () => {
    await signInWithRedirect();
  }, []);

  const signOut = useCallback(async () => {
    await amplifySignOut();
    setUser(null);
    window.location.href = '/login';
  }, []);

  const getAccessToken = useCallback(async (): Promise<string | null> => {
    try {
      const session = await fetchAuthSession();
      const token = session.tokens?.idToken?.toString();
      return token || null;
    } catch {
      return null;
    }
  }, []);

  return {
    user,
    isAuthenticated: !!user,
    isLoading,
    signIn,
    signOut,
    getAccessToken,
  };
}
