import { useEffect, useState } from 'react';
import { Chart as ChartJS, ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, Filler } from 'chart.js';
import { Doughnut, Bar, Line } from 'react-chartjs-2';
import { budgetService, dashboardService, expenseService, insightService } from '../services';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, formatCategory, formatDate } from '../utils/formatters';

ChartJS.register(ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, Filler);

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [budget, setBudget] = useState(null);
  const [recentExpenses, setRecentExpenses] = useState([]);
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();

    Promise.all([
      dashboardService.getDashboard(),
      budgetService.getStatus(month, year).catch(() => null),
      expenseService.getAll(),
      insightService.getInsights(),
    ]).then(([dashRes, budgetRes, expRes, insightRes]) => {
      setDashboard(dashRes.data.data);
      setBudget(budgetRes?.data?.data ?? null);
      setRecentExpenses(expRes.data.data.slice(0, 5));
      setInsights(insightRes.data.data.slice(0, 3));
    }).finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner size="lg" className="py-20" />;
  if (!dashboard) return null;

  const categoryLabels = Object.keys(dashboard.categoryWiseSpending || {});
  const categoryValues = Object.values(dashboard.categoryWiseSpending || {});

  const categoryChartData = {
    labels: categoryLabels.map(formatCategory),
    datasets: [{
      data: categoryValues,
      backgroundColor: ['#6366f1', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981', '#06b6d4', '#ef4444', '#84cc16', '#f97316', '#64748b'],
      borderWidth: 0,
    }],
  };

  const trendLabels = dashboard.monthlyTrend?.map((t) => t.month) || [];
  const trendChartData = {
    labels: trendLabels,
    datasets: [
      {
        label: 'Income',
        data: dashboard.monthlyTrend?.map((t) => t.income) || [],
        borderColor: '#10b981',
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        fill: true,
        tension: 0.4,
      },
      {
        label: 'Expense',
        data: dashboard.monthlyTrend?.map((t) => t.expense) || [],
        borderColor: '#ef4444',
        backgroundColor: 'rgba(239, 68, 68, 0.1)',
        fill: true,
        tension: 0.4,
      },
    ],
  };

  const barChartData = {
    labels: trendLabels,
    datasets: [{
      label: 'Monthly Spending',
      data: dashboard.monthlyTrend?.map((t) => t.expense) || [],
      backgroundColor: '#6366f1',
      borderRadius: 8,
    }],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
  };

  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard
          title="Total Income"
          value={formatCurrency(dashboard.totalIncome)}
          color="green"
          icon={<span className="text-lg">↑</span>}
        />
        <StatCard
          title="Total Expense"
          value={formatCurrency(dashboard.totalExpense)}
          color="red"
          icon={<span className="text-lg">↓</span>}
        />
        <StatCard
          title="Current Balance"
          value={formatCurrency(dashboard.currentBalance)}
          color="primary"
          icon={<span className="text-lg">₹</span>}
        />
        <StatCard
          title="Monthly Spending"
          value={formatCurrency(dashboard.monthlyTrend?.at(-1)?.expense ?? 0)}
          subtitle="This month"
          color="amber"
          icon={<span className="text-lg">📊</span>}
        />
      </div>

      {budget && (
        <div className="card p-6">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold">Budget Progress</h3>
            <span className={`text-sm font-medium ${budget.warning ? 'text-rose-600' : 'text-emerald-600'}`}>
              {budget.percentUsed?.toFixed(1)}% used
            </span>
          </div>
          <div className="mt-3 h-3 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
            <div
              className={`h-full rounded-full transition-all ${budget.warning ? 'bg-rose-500' : 'bg-primary-500'}`}
              style={{ width: `${Math.min(budget.percentUsed, 100)}%` }}
            />
          </div>
          <div className="mt-2 flex justify-between text-xs text-slate-500">
            <span>Spent: {formatCurrency(budget.totalSpent)}</span>
            <span>Limit: {formatCurrency(budget.monthlyLimit)}</span>
          </div>
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Category Breakdown</h3>
          <div className="h-64">
            {categoryLabels.length > 0 ? (
              <Doughnut data={categoryChartData} options={{ ...chartOptions, plugins: { legend: { position: 'right' } } }} />
            ) : (
              <p className="flex h-full items-center justify-center text-sm text-slate-400">No expense data yet</p>
            )}
          </div>
        </div>
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Monthly Trends</h3>
          <div className="h-64">
            <Line data={trendChartData} options={chartOptions} />
          </div>
        </div>
      </div>

      <div className="card p-6">
        <h3 className="mb-4 font-semibold">Monthly Spending</h3>
        <div className="h-48">
          <Bar data={barChartData} options={{ ...chartOptions, plugins: { legend: { display: false } } }} />
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">Recent Transactions</h3>
          {recentExpenses.length === 0 ? (
            <p className="text-sm text-slate-400">No transactions yet</p>
          ) : (
            <div className="space-y-3">
              {recentExpenses.map((exp) => (
                <div key={exp.id} className="flex items-center justify-between rounded-xl bg-slate-50 p-3 dark:bg-slate-800/50">
                  <div>
                    <p className="text-sm font-medium">{exp.description || formatCategory(exp.category)}</p>
                    <p className="text-xs text-slate-500">{formatDate(exp.expenseDate)}</p>
                  </div>
                  <span className="text-sm font-semibold text-rose-600">-{formatCurrency(exp.amount)}</span>
                </div>
              ))}
            </div>
          )}
        </div>
        <div className="card p-6">
          <h3 className="mb-4 font-semibold">AI Insights</h3>
          {insights.length === 0 ? (
            <p className="text-sm text-slate-400">No insights available</p>
          ) : (
            <div className="space-y-3">
              {insights.map((insight, i) => (
                <div
                  key={i}
                  className={`rounded-xl p-3 text-sm ${
                    insight.type === 'WARNING'
                      ? 'bg-rose-50 text-rose-800 dark:bg-rose-900/20 dark:text-rose-300'
                      : insight.type === 'TIP'
                      ? 'bg-emerald-50 text-emerald-800 dark:bg-emerald-900/20 dark:text-emerald-300'
                      : 'bg-primary-50 text-primary-800 dark:bg-primary-900/20 dark:text-primary-300'
                  }`}
                >
                  {insight.message}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
