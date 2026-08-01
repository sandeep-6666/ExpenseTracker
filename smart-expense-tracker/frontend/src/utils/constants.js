export const API_ENDPOINTS = {
  auth: {
    register: '/auth/register',
    login: '/auth/login',
  },
  users: {
    profile: '/api/users/me',
    password: '/api/users/me/password',
  },
  expenses: {
    base: '/api/expenses',
    search: '/api/expenses/search',
    filterCategory: '/api/expenses/filter/category',
    filterDate: '/api/expenses/filter/date',
    filterMonth: '/api/expenses/filter/month',
    filterAmount: '/api/expenses/filter/amount',
  },
  incomes: {
    base: '/api/incomes',
  },
  budgets: {
    base: '/api/budgets',
    status: '/api/budgets/status',
  },
  dashboard: '/api/dashboard',
  insights: '/api/insights',
};

export const EXPENSE_CATEGORIES = [
  'FOOD', 'SHOPPING', 'BILLS', 'FUEL', 'TRAVEL',
  'MEDICAL', 'EDUCATION', 'ENTERTAINMENT', 'INVESTMENT', 'OTHER',
];

export const STORAGE_KEYS = {
  token: 'et_token',
  user: 'et_user',
  theme: 'et_theme',
};

export const PAGE_SIZE = 10;
