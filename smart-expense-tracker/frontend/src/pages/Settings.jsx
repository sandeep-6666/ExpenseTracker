import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { userService } from '../services';
import { useTheme } from '../context/ThemeContext';

export default function Settings() {
  const { darkMode, toggleTheme } = useTheme();
  const [saving, setSaving] = useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm();

  const onSubmit = async (data) => {
    setSaving(true);
    try {
      await userService.changePassword(data);
      toast.success('Password changed successfully');
    } catch {
      // handled
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <div className="card p-6">
        <h3 className="font-semibold">Appearance</h3>
        <div className="mt-4 flex items-center justify-between">
          <div>
            <p className="text-sm font-medium">Dark Mode</p>
            <p className="text-xs text-slate-500">Toggle between light and dark theme</p>
          </div>
          <button
            type="button"
            onClick={toggleTheme}
            className={`relative h-7 w-12 rounded-full transition ${darkMode ? 'bg-primary-600' : 'bg-slate-300'}`}
          >
            <span
              className={`absolute top-0.5 h-6 w-6 rounded-full bg-white shadow transition ${darkMode ? 'left-5' : 'left-0.5'}`}
            />
          </button>
        </div>
      </div>

      <div className="card p-6">
        <h3 className="font-semibold">Change Password</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="mt-4 space-y-4">
          <div>
            <label className="label">Current Password</label>
            <input type="password" className="input-field" {...register('currentPassword', { required: 'Required' })} />
            {errors.currentPassword && <p className="mt-1 text-xs text-rose-500">{errors.currentPassword.message}</p>}
          </div>
          <div>
            <label className="label">New Password</label>
            <input
              type="password"
              className="input-field"
              {...register('newPassword', { required: 'Required', minLength: { value: 6, message: 'Minimum 6 characters' } })}
            />
            {errors.newPassword && <p className="mt-1 text-xs text-rose-500">{errors.newPassword.message}</p>}
          </div>
          <button type="submit" disabled={saving} className="btn-primary">
            {saving ? 'Updating...' : 'Update Password'}
          </button>
        </form>
      </div>

      <div className="card p-6">
        <h3 className="font-semibold">About</h3>
        <p className="mt-2 text-sm text-slate-500">
          Smart Expense Tracker v1.0 — Track expenses, manage budgets, and get AI-powered insights.
        </p>
      </div>
    </div>
  );
}
