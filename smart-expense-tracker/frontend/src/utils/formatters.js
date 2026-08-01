export const formatCurrency = (value) => {
  const num = Number(value ?? 0);
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
  }).format(num);
};

export const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const formatCategory = (category) =>
  category?.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase()) ?? '';

export const getMonthName = (month) =>
  new Date(2000, month - 1, 1).toLocaleString('en-US', { month: 'long' });
