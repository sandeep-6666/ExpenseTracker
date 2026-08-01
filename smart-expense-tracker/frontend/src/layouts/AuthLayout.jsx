export default function AuthLayout({ children }) {
  return (
    <div className="flex min-h-screen">
      <div className="hidden w-1/2 flex-col justify-between bg-gradient-to-br from-primary-600 via-primary-700 to-primary-900 p-12 text-white lg:flex">
        <div>
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-white/20 text-xl font-bold backdrop-blur">
            ET
          </div>
          <h1 className="mt-8 text-4xl font-bold leading-tight">
            Smart Expense<br />Tracker
          </h1>
          <p className="mt-4 max-w-sm text-primary-100">
            Take control of your finances with intelligent insights, budget tracking, and beautiful analytics.
          </p>
        </div>
        <p className="text-sm text-primary-200">Track smarter. Spend wiser.</p>
      </div>
      <div className="flex flex-1 items-center justify-center p-6">
        <div className="w-full max-w-md">{children}</div>
      </div>
    </div>
  );
}
