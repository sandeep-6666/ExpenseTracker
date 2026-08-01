import { useCallback, useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { expenseService } from '../services';
import Modal from '../components/Modal';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import { usePagination, useSort } from '../hooks/usePagination';
import { EXPENSE_CATEGORIES, PAGE_SIZE } from '../utils/constants';
import { formatCurrency, formatCategory, formatDate } from '../utils/formatters';

export default function Expenses() {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [sortField, setSortField] = useState('expenseDate');
  const [sortDir, setSortDir] = useState('desc');

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  const fetchExpenses = useCallback(async () => {
    setLoading(true);
    try {
      let res;
      if (search) {
        res = await expenseService.search(search);
      } else if (categoryFilter) {
        res = await expenseService.filterByCategory(categoryFilter);
      } else {
        res = await expenseService.getAll();
      }
      setExpenses(res.data.data);
    } finally {
      setLoading(false);
    }
  }, [search, categoryFilter]);

  useEffect(() => { fetchExpenses(); }, [fetchExpenses]);

  const filtered = useMemo(() => {
    let items = [...expenses];
    items.sort((a, b) => {
      let aVal = a[sortField];
      let bVal = b[sortField];
      if (typeof aVal === 'string') aVal = aVal.toLowerCase();
      if (typeof bVal === 'string') bVal = bVal.toLowerCase();
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
    return items;
  }, [expenses, sortField, sortDir]);

  const { page, totalPages, paginatedItems, goToPage, resetPage } = usePagination(filtered, PAGE_SIZE);

  useEffect(() => { resetPage(); }, [search, categoryFilter, resetPage]);

  const openCreate = () => {
    setEditing(null);
    reset({ amount: '', category: 'FOOD', description: '', expenseDate: new Date().toISOString().split('T')[0] });
    setModalOpen(true);
  };

  const openEdit = (expense) => {
    setEditing(expense);
    reset({
      amount: expense.amount,
      category: expense.category,
      description: expense.description,
      expenseDate: expense.expenseDate,
    });
    setModalOpen(true);
  };

  const onSubmit = async (data) => {
    const payload = { ...data, amount: parseFloat(data.amount) };
    try {
      if (editing) {
        await expenseService.update(editing.id, payload);
        toast.success('Expense updated');
      } else {
        await expenseService.create(payload);
        toast.success('Expense added');
      }
      setModalOpen(false);
      fetchExpenses();
    } catch {
      // handled by interceptor
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this expense?')) return;
    try {
      await expenseService.remove(id);
      toast.success('Expense deleted');
      fetchExpenses();
    } catch {
      // handled
    }
  };

  const toggleSort = (field) => {
    if (sortField === field) setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    else { setSortField(field); setSortDir('asc'); }
  };

  const SortIcon = ({ field }) => (
    <span className="ml-1 text-xs">{sortField === field ? (sortDir === 'asc' ? '↑' : '↓') : '↕'}</span>
  );

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-1 gap-2">
          <input
            type="text"
            placeholder="Search expenses..."
            className="input-field max-w-xs"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <select
            className="input-field max-w-[160px]"
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
          >
            <option value="">All Categories</option>
            {EXPENSE_CATEGORIES.map((c) => (
              <option key={c} value={c}>{formatCategory(c)}</option>
            ))}
          </select>
        </div>
        <button type="button" onClick={openCreate} className="btn-primary">
          + Add Expense
        </button>
      </div>

      <div className="card overflow-hidden">
        {loading ? (
          <LoadingSpinner className="py-12" />
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 dark:border-slate-800 dark:bg-slate-800/50">
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('description')}>
                      Description<SortIcon field="description" />
                    </th>
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('category')}>
                      Category<SortIcon field="category" />
                    </th>
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('amount')}>
                      Amount<SortIcon field="amount" />
                    </th>
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('expenseDate')}>
                      Date<SortIcon field="expenseDate" />
                    </th>
                    <th className="px-4 py-3 text-right font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedItems.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="px-4 py-8 text-center text-slate-400">No expenses found</td>
                    </tr>
                  ) : (
                    paginatedItems.map((exp) => (
                      <tr key={exp.id} className="border-b border-slate-100 dark:border-slate-800">
                        <td className="px-4 py-3">{exp.description || '-'}</td>
                        <td className="px-4 py-3">
                          <span className="rounded-lg bg-primary-50 px-2 py-0.5 text-xs font-medium text-primary-700 dark:bg-primary-900/30 dark:text-primary-300">
                            {formatCategory(exp.category)}
                          </span>
                        </td>
                        <td className="px-4 py-3 font-medium text-rose-600">{formatCurrency(exp.amount)}</td>
                        <td className="px-4 py-3 text-slate-500">{formatDate(exp.expenseDate)}</td>
                        <td className="px-4 py-3 text-right">
                          <button type="button" onClick={() => openEdit(exp)} className="mr-2 text-primary-600 hover:underline text-xs">Edit</button>
                          <button type="button" onClick={() => handleDelete(exp.id)} className="text-rose-600 hover:underline text-xs">Delete</button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
            <div className="p-4">
              <Pagination page={page} totalPages={totalPages} onPageChange={goToPage} />
            </div>
          </>
        )}
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit Expense' : 'Add Expense'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label">Amount</label>
            <input type="number" step="0.01" className="input-field" {...register('amount', { required: 'Required', min: 0.01 })} />
            {errors.amount && <p className="mt-1 text-xs text-rose-500">{errors.amount.message}</p>}
          </div>
          <div>
            <label className="label">Category</label>
            <select className="input-field" {...register('category', { required: true })}>
              {EXPENSE_CATEGORIES.map((c) => (
                <option key={c} value={c}>{formatCategory(c)}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Description</label>
            <input className="input-field" {...register('description')} />
          </div>
          <div>
            <label className="label">Date</label>
            <input type="date" className="input-field" {...register('expenseDate', { required: true })} />
          </div>
          <div className="flex gap-2 pt-2">
            <button type="submit" className="btn-primary flex-1">{editing ? 'Update' : 'Add'}</button>
            <button type="button" onClick={() => setModalOpen(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
