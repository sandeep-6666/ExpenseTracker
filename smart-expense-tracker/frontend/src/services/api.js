import axios from 'axios';
import { toast } from 'react-toastify';
import { API_ENDPOINTS, STORAGE_KEYS } from '../utils/constants';
import { storage } from '../utils/storage';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = storage.get(STORAGE_KEYS.token);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 || error.response?.status === 403) {
      if (originalRequest.url?.includes(API_ENDPOINTS.auth.login) ||
          originalRequest.url?.includes(API_ENDPOINTS.auth.register)) {
        return Promise.reject(error);
      }

      storage.clearAuth();
      window.location.href = '/login';
      return Promise.reject(error);
    }

    const fieldErrors = error.response?.data?.fieldErrors;
    const message =
      (fieldErrors && Object.values(fieldErrors).join(', ')) ||
      error.response?.data?.message ||
      'Something went wrong';

    if (!originalRequest._silent) {
      toast.error(message);
    }

    return Promise.reject(error);
  }
);

export default api;
