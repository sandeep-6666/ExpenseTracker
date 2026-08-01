import { useMemo, useState } from 'react';

export const usePagination = (items, pageSize = 10) => {
  const [page, setPage] = useState(1);

  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));

  const paginatedItems = useMemo(() => {
    const start = (page - 1) * pageSize;
    return items.slice(start, start + pageSize);
  }, [items, page, pageSize]);

  const goToPage = (newPage) => {
    setPage(Math.min(Math.max(1, newPage), totalPages));
  };

  const resetPage = () => setPage(1);

  return { page, totalPages, paginatedItems, goToPage, resetPage, setPage };
};

export const useSort = (items, defaultField = 'expenseDate', defaultDir = 'desc') => {
  const [sortField, setSortField] = useState(defaultField);
  const [sortDir, setSortDir] = useState(defaultDir);

  const sortedItems = useMemo(() => {
    return [...items].sort((a, b) => {
      let aVal = a[sortField];
      let bVal = b[sortField];
      if (typeof aVal === 'string') aVal = aVal.toLowerCase();
      if (typeof bVal === 'string') bVal = bVal.toLowerCase();
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }, [items, sortField, sortDir]);

  const toggleSort = (field) => {
    if (sortField === field) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  };

  return { sortedItems, sortField, sortDir, toggleSort };
};
