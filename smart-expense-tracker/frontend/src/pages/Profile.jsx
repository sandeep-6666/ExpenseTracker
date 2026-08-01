import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatDate } from '../utils/formatters';

export default function Profile() {
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    userService.getProfile()
      .then((res) => {
        setProfile(res.data.data);
        reset({ name: res.data.data.name });
      })
      .finally(() => setLoading(false));
  }, [reset]);

  const onSubmit = async (data) => {
    setSaving(true);
    try {
      const res = await userService.updateProfile(data);
      setProfile(res.data.data);
      updateUser({ name: res.data.data.name });
      toast.success('Profile updated');
    } catch {
      // handled
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner size="lg" className="py-20" />;

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <div className="card p-6">
        <div className="flex items-center gap-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-500 to-primary-700 text-2xl font-bold text-white">
            {profile?.name?.charAt(0)?.toUpperCase()}
          </div>
          <div>
            <h2 className="text-xl font-bold">{profile?.name}</h2>
            <p className="text-sm text-slate-500">{profile?.email}</p>
            <span className="mt-1 inline-block rounded-lg bg-primary-50 px-2 py-0.5 text-xs font-medium text-primary-700 dark:bg-primary-900/30 dark:text-primary-300">
              {profile?.role}
            </span>
          </div>
        </div>
        <div className="mt-4 text-sm text-slate-500">
          Member since {formatDate(profile?.createdAt)}
        </div>
      </div>

      <div className="card p-6">
        <h3 className="font-semibold">Edit Profile</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="mt-4 space-y-4">
          <div>
            <label className="label">Full Name</label>
            <input className="input-field" {...register('name', { required: 'Name is required' })} />
            {errors.name && <p className="mt-1 text-xs text-rose-500">{errors.name.message}</p>}
          </div>
          <div>
            <label className="label">Email</label>
            <input className="input-field bg-slate-50 dark:bg-slate-800" value={user?.email || ''} disabled />
            <p className="mt-1 text-xs text-slate-400">Email cannot be changed</p>
          </div>
          <button type="submit" disabled={saving} className="btn-primary">
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  );
}
