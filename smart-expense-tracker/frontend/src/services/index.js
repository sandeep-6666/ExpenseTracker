import api from './api';
import { API_ENDPOINTS } from '../utils/constants';

export const authService = {
  register: (data) => api.post(API_ENDPOINTS.auth.register, data),
  login: (data) => api.post(API_ENDPOINTS.auth.login, data),
};

export const expenseService = {
  getAll: () => api.get(API_ENDPOINTS.expenses.base),
  getById: (id) => api.get(`${API_ENDPOINTS.expenses.base}/${id}`),
  create: (data) => api.post(API_ENDPOINTS.expenses.base, data),
  update: (id, data) => api.put(`${API_ENDPOINTS.expenses.base}/${id}`, data),
  remove: (id) => api.delete(`${API_ENDPOINTS.expenses.base}/${id}`),
  search: (keyword) => api.get(API_ENDPOINTS.expenses.search, { params: { keyword } }),
  filterByCategory: (category) => api.get(API_ENDPOINTS.expenses.filterCategory, { params: { category } }),
  filterByDate: (start, end) => api.get(API_ENDPOINTS.expenses.filterDate, { params: { start, end } }),
  filterByMonth: (month, year) => api.get(API_ENDPOINTS.expenses.filterMonth, { params: { month, year } }),
  filterByAmount: (min, max) => api.get(API_ENDPOINTS.expenses.filterAmount, { params: { min, max } }),
};

export const incomeService = {
  getAll: () => api.get(API_ENDPOINTS.incomes.base),
  getById: (id) => api.get(`${API_ENDPOINTS.incomes.base}/${id}`),
  create: (data) => api.post(API_ENDPOINTS.incomes.base, data),
  update: (id, data) => api.put(`${API_ENDPOINTS.incomes.base}/${id}`, data),
  remove: (id) => api.delete(`${API_ENDPOINTS.incomes.base}/${id}`),
};

export const budgetService = {
  setBudget: (data) => api.post(API_ENDPOINTS.budgets.base, data),
  getStatus: (month, year) => api.get(API_ENDPOINTS.budgets.status, { params: { month, year }, _silent: true }),
};

export const dashboardService = {
  getDashboard: () => api.get(API_ENDPOINTS.dashboard),
};

export const insightService = {
  getInsights: () => api.get(API_ENDPOINTS.insights),
};

export const userService = {
  getProfile: () => api.get(API_ENDPOINTS.users.profile),
  updateProfile: (data) => api.put(API_ENDPOINTS.users.profile, data),
  changePassword: (data) => api.put(API_ENDPOINTS.users.password, data),
};
