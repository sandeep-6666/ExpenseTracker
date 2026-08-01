import { useEffect, useState } from 'react';
import { Chart as ChartJS, ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, Filler } from 'chart.js';
import { Doughnut, Bar, Line } from 'react-chartjs-2';
import { dashboardService, insightService } from '../services';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, formatCategory } from '../utils/formatters';

ChartJS.register(ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, Filler);

export default function Analytics() {
  const [dashboard, setDashboard] = useState(null);
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([dashboardService.getDashboard(), insightService.getInsights()])
      .then(([dashRes, insightRes]) => {
        setDashboard(dashRes.data.data);
        setInsights(insightRes.data.data);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner size="lg" className="py-20" />;
  if (!dashboard) return null;

  const categoryLabels = Object.keys(dashboard.categoryWiseSpending || {});
  const categoryValues = Object.values(dashboard.categoryWiseSpending || {});

  const pieData = {
    labels: categoryLabels.map(formatCategory),
    datasets: [{
      data: categoryValues,
      backgroundColor: ['#6366f1', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981', '#06b6d4', '#ef4444', '#84cc16'],
      borderWidth: 0,
    }],
  };

  const trendLabels = dashboard.monthlyTrend?.map((t) => t.month) || [];
  const lineData = {
    labels: trendLabels,
    datasets: [
      { label: 'Income', data: dashboard.monthlyTrend?.map((t) => t.income), borderColor: '#10b981', backgroundColor: 'rgba(16,185,129,0.1)', fill: true, tension: 0.4 },
      { label: 'Expense', data: dashboard.monthlyTrend?.map((t) => t.expense), borderColor: '#ef4444', backgroundColor: 'rgba(239,68,68,0.1)', fill: true, tension: 0.4 },
    ],
  };

  const barData = {
    labels: categoryLabels.map(formatCategory),
    datasets: [{ label: 'Spending by Category', data: categoryValues, backgroundColor: '#6366f1', borderRadius: 8 }],
  };

  const savingsData = {
    labels: trendLabels,
    datasets: [{
      label: 'Net Savings',
      data: dashboard.monthlyTrend?.map((t) => (t.income || 0) - (t.expense || 0)),
      backgroundColor: dashboard.monthlyTrend?.map((t) => ((t.income || 0) - (t.expense || 0)) >= 0 ? '#10b981' : '#ef4444'),
      borderRadius: 8,
    }],
  };

  const chartOpts = { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } };

  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-3">
        <div className="card p-5 text-center">
          <p className="text-sm text-slate-500">Total Income</p>
          <p className="mt-1 text-2xl font-bold text-emerald-600">{formatCurrency(dashboard.totalIncome)}</p>
        </div>
        <div className="card p-5 text-center">
          <p className="text-sm text-slate-500">Total Expense</p>
          <p className="mt-1 text-2xl font-bold text-rose-600">{formatCurrency(dashboard.totalExpense)}</p>
        </div>
        <div className="card p-5 text-center">
          <p className="text-sm text-slate-500">Savings Rate</p>
          <p className="mt-1 text-2xl font-bold text-primary-600">
            {dashboard.totalIncome > 0
              ? `${(((dashboard.totalIncome - dashboard.totalExpense) / dashboard.totalIncome) * 100).toFixed(1)}%`
              : '0%'}
          </p>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Spending by Category</h3>
          <div className="h-72">
            {categoryLabels.length > 0 ? (
              <Doughnut data={pieData} options={chartOpts} />
            ) : (
              <p className="flex h-full items-center justify-center text-slate-400">No data</p>
            )}
          </div>
        </div>
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Category Comparison</h3>
          <div className="h-72">
            {categoryLabels.length > 0 ? (
              <Bar data={barData} options={{ ...chartOpts, indexAxis: 'y' }} />
            ) : (
              <p className="flex h-full items-center justify-center text-slate-400">No data</p>
            )}
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Income vs Expense Trend</h3>
          <div className="h-72"><Line data={lineData} options={chartOpts} /></div>
        </div>
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Monthly Net Savings</h3>
          <div className="h-72"><Bar data={savingsData} options={{ ...chartOpts, plugins: { legend: { display: false } } }} /></div>
        </div>
      </div>

      <div className="card p-6">
        <h3 className="mb-4 font-semibold">AI Insights</h3>
        {insights.length === 0 ? (
          <p className="text-sm text-slate-400">No insights available yet. Add more transactions to get personalized tips.</p>
        ) : (
          <div className="grid gap-3 sm:grid-cols-2">
            {insights.map((insight, i) => (
              <div
                key={i}
                className={`rounded-xl p-4 text-sm ${
                  insight.type === 'WARNING'
                    ? 'border border-rose-200 bg-rose-50 dark:border-rose-800 dark:bg-rose-900/20'
                    : insight.type === 'TIP'
                    ? 'border border-emerald-200 bg-emerald-50 dark:border-emerald-800 dark:bg-emerald-900/20'
                    : 'border border-primary-200 bg-primary-50 dark:border-primary-800 dark:bg-primary-900/20'
                }`}
              >
                <span className="text-xs font-bold uppercase tracking-wide opacity-60">{insight.type}</span>
                <p className="mt-1">{insight.message}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
