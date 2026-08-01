import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { budgetService } from '../services';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, getMonthName } from '../utils/formatters';

export default function Budgets() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [budget, setBudget] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  const fetchBudget = async () => {
    setLoading(true);
    try {
      const res = await budgetService.getStatus(month, year);
      setBudget(res.data.data);
      reset({ monthlyLimit: res.data.data.monthlyLimit, month, year });
    } catch {
      setBudget(null);
      reset({ monthlyLimit: '', month, year });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBudget(); }, [month, year]);

  const onSubmit = async (data) => {
    setSaving(true);
    try {
      const payload = {
        month: parseInt(data.month),
        year: parseInt(data.year),
        monthlyLimit: parseFloat(data.monthlyLimit),
      };
      const res = await budgetService.setBudget(payload);
      setBudget(res.data.data);
      toast.success('Budget saved successfully');
    } catch {
      // handled
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div className="card p-6">
        <h3 className="font-semibold">Set Monthly Budget</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="mt-4 space-y-4">
          <div className="grid gap-4 sm:grid-cols-3">
            <div>
              <label className="label">Month</label>
              <select className="input-field" value={month} onChange={(e) => setMonth(parseInt(e.target.value))}>
                {Array.from({ length: 12 }, (_, i) => (
                  <option key={i + 1} value={i + 1}>{getMonthName(i + 1)}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Year</label>
              <select className="input-field" value={year} onChange={(e) => setYear(parseInt(e.target.value))}>
                {[2024, 2025, 2026, 2027].map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Monthly Limit</label>
              <input
                type="number"
                step="0.01"
                className="input-field"
                {...register('monthlyLimit', { required: 'Required', min: 1 })}
              />
              {errors.monthlyLimit && <p className="mt-1 text-xs text-rose-500">{errors.monthlyLimit.message}</p>}
            </div>
          </div>
          <button type="submit" disabled={saving} className="btn-primary">
            {saving ? 'Saving...' : 'Save Budget'}
          </button>
        </form>
      </div>

      {loading ? (
        <LoadingSpinner className="py-12" />
      ) : budget ? (
        <div className="card p-6">
          <h3 className="font-semibold">
            Budget Status — {getMonthName(budget.month)} {budget.year}
          </h3>
          <div className="mt-6 grid gap-4 sm:grid-cols-3">
            <div className="rounded-xl bg-slate-50 p-4 dark:bg-slate-800/50">
              <p className="text-xs text-slate-500">Monthly Limit</p>
              <p className="mt-1 text-lg font-bold">{formatCurrency(budget.monthlyLimit)}</p>
            </div>
            <div className="rounded-xl bg-slate-50 p-4 dark:bg-slate-800/50">
              <p className="text-xs text-slate-500">Total Spent</p>
              <p className="mt-1 text-lg font-bold text-rose-600">{formatCurrency(budget.totalSpent)}</p>
            </div>
            <div className="rounded-xl bg-slate-50 p-4 dark:bg-slate-800/50">
              <p className="text-xs text-slate-500">Remaining</p>
              <p className="mt-1 text-lg font-bold text-emerald-600">{formatCurrency(budget.remaining)}</p>
            </div>
          </div>
          <div className="mt-6">
            <div className="flex justify-between text-sm">
              <span>{budget.percentUsed?.toFixed(1)}% used</span>
              {budget.warning && <span className="font-medium text-rose-600">⚠ Budget warning (80%+)</span>}
            </div>
            <div className="mt-2 h-4 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
              <div
                className={`h-full rounded-full transition-all duration-500 ${budget.warning ? 'bg-rose-500' : 'bg-primary-500'}`}
                style={{ width: `${Math.min(budget.percentUsed, 100)}%` }}
              />
            </div>
          </div>
        </div>
      ) : (
        <div className="card p-8 text-center text-slate-400">
          No budget set for {getMonthName(month)} {year}. Set one above.
        </div>
      )}
    </div>
  );
}
