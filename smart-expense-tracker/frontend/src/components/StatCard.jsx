export default function StatCard({ title, value, subtitle, icon, trend, color = 'primary' }) {
  const colors = {
    primary: 'from-primary-500 to-primary-700',
    green: 'from-emerald-500 to-emerald-700',
    red: 'from-rose-500 to-rose-700',
    amber: 'from-amber-500 to-amber-700',
  };

  return (
    <div className="card p-6 transition hover:shadow-md">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-slate-500 dark:text-slate-400">{title}</p>
          <p className="mt-2 text-2xl font-bold tracking-tight">{value}</p>
          {subtitle && (
            <p className="mt-1 text-xs text-slate-500 dark:text-slate-400">{subtitle}</p>
          )}
          {trend && (
            <p className={`mt-2 text-xs font-medium ${trend.positive ? 'text-emerald-600' : 'text-rose-600'}`}>
              {trend.label}
            </p>
          )}
        </div>
        {icon && (
          <div className={`flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br ${colors[color]} text-white shadow-lg`}>
            {icon}
          </div>
        )}
      </div>
    </div>
  );
}
