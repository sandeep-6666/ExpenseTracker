import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services';
import { STORAGE_KEYS } from '../utils/constants';
import { storage } from '../utils/storage';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => storage.get(STORAGE_KEYS.user));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = storage.get(STORAGE_KEYS.token);
    if (!token) {
      setLoading(false);
      return;
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const { data } = await authService.login(credentials);
    const authData = data.data;
    storage.set(STORAGE_KEYS.token, authData.token);
    storage.set(STORAGE_KEYS.user, authData.user);
    setUser(authData.user);
    return authData;
  };

  const register = async (payload) => {
    const { data } = await authService.register(payload);
    const authData = data.data;
    storage.set(STORAGE_KEYS.token, authData.token);
    storage.set(STORAGE_KEYS.user, authData.user);
    setUser(authData.user);
    return authData;
  };

  const logout = () => {
    storage.clearAuth();
    setUser(null);
  };

  const updateUser = (updates) => {
    const updated = { ...user, ...updates };
    storage.set(STORAGE_KEYS.user, updated);
    setUser(updated);
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: !!user && !!storage.get(STORAGE_KEYS.token),
      login,
      register,
      logout,
      updateUser,
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
