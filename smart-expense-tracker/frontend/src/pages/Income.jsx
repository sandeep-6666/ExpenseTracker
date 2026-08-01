import { useCallback, useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { incomeService } from '../services';
import Modal from '../components/Modal';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import { usePagination } from '../hooks/usePagination';
import { PAGE_SIZE } from '../utils/constants';
import { formatCurrency, formatDate } from '../utils/formatters';

export default function Income() {
  const [incomes, setIncomes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const [sortField, setSortField] = useState('incomeDate');
  const [sortDir, setSortDir] = useState('desc');

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  const fetchIncomes = useCallback(async () => {
    setLoading(true);
    try {
      const res = await incomeService.getAll();
      setIncomes(res.data.data);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchIncomes(); }, [fetchIncomes]);

  const filtered = useMemo(() => {
    let items = incomes.filter((i) =>
      !search || i.source.toLowerCase().includes(search.toLowerCase())
    );
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
  }, [incomes, search, sortField, sortDir]);

  const { page, totalPages, paginatedItems, goToPage, resetPage } = usePagination(filtered, PAGE_SIZE);
  useEffect(() => { resetPage(); }, [search, resetPage]);

  const openCreate = () => {
    setEditing(null);
    reset({ amount: '', source: '', incomeDate: new Date().toISOString().split('T')[0] });
    setModalOpen(true);
  };

  const openEdit = (income) => {
    setEditing(income);
    reset({ amount: income.amount, source: income.source, incomeDate: income.incomeDate });
    setModalOpen(true);
  };

  const onSubmit = async (data) => {
    const payload = { ...data, amount: parseFloat(data.amount) };
    try {
      if (editing) {
        await incomeService.update(editing.id, payload);
        toast.success('Income updated');
      } else {
        await incomeService.create(payload);
        toast.success('Income added');
      }
      setModalOpen(false);
      fetchIncomes();
    } catch {
      // handled
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this income entry?')) return;
    try {
      await incomeService.remove(id);
      toast.success('Income deleted');
      fetchIncomes();
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
        <input
          type="text"
          placeholder="Search by source..."
          className="input-field max-w-xs"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button type="button" onClick={openCreate} className="btn-primary">+ Add Income</button>
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
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('source')}>
                      Source<SortIcon field="source" />
                    </th>
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('amount')}>
                      Amount<SortIcon field="amount" />
                    </th>
                    <th className="cursor-pointer px-4 py-3 text-left font-medium" onClick={() => toggleSort('incomeDate')}>
                      Date<SortIcon field="incomeDate" />
                    </th>
                    <th className="px-4 py-3 text-right font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedItems.length === 0 ? (
                    <tr><td colSpan={4} className="px-4 py-8 text-center text-slate-400">No income entries found</td></tr>
                  ) : (
                    paginatedItems.map((inc) => (
                      <tr key={inc.id} className="border-b border-slate-100 dark:border-slate-800">
                        <td className="px-4 py-3 font-medium">{inc.source}</td>
                        <td className="px-4 py-3 font-medium text-emerald-600">{formatCurrency(inc.amount)}</td>
                        <td className="px-4 py-3 text-slate-500">{formatDate(inc.incomeDate)}</td>
                        <td className="px-4 py-3 text-right">
                          <button type="button" onClick={() => openEdit(inc)} className="mr-2 text-primary-600 hover:underline text-xs">Edit</button>
                          <button type="button" onClick={() => handleDelete(inc.id)} className="text-rose-600 hover:underline text-xs">Delete</button>
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

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit Income' : 'Add Income'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label">Source</label>
            <input className="input-field" {...register('source', { required: 'Required' })} />
            {errors.source && <p className="mt-1 text-xs text-rose-500">{errors.source.message}</p>}
          </div>
          <div>
            <label className="label">Amount</label>
            <input type="number" step="0.01" className="input-field" {...register('amount', { required: 'Required', min: 0.01 })} />
            {errors.amount && <p className="mt-1 text-xs text-rose-500">{errors.amount.message}</p>}
          </div>
          <div>
            <label className="label">Date</label>
            <input type="date" className="input-field" {...register('incomeDate', { required: true })} />
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
