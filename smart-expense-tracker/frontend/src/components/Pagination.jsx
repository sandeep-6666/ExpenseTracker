export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);
  const visiblePages = pages.filter(
    (p) => p === 1 || p === totalPages || Math.abs(p - page) <= 1
  );

  return (
    <div className="flex items-center justify-center gap-1 pt-4">
      <button
        type="button"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 1}
        className="btn-secondary px-3 py-1.5 text-xs disabled:opacity-40"
      >
        Prev
      </button>
      {visiblePages.map((p, idx) => {
        const prev = visiblePages[idx - 1];
        const showEllipsis = prev && p - prev > 1;
        return (
          <span key={p} className="flex items-center gap-1">
            {showEllipsis && <span className="px-1 text-slate-400">...</span>}
            <button
              type="button"
              onClick={() => onPageChange(p)}
              className={`min-w-[2rem] rounded-lg px-2 py-1.5 text-xs font-medium transition ${
                p === page
                  ? 'bg-primary-600 text-white'
                  : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'
              }`}
            >
              {p}
            </button>
          </span>
        );
      })}
      <button
        type="button"
        onClick={() => onPageChange(page + 1)}
        disabled={page === totalPages}
        className="btn-secondary px-3 py-1.5 text-xs disabled:opacity-40"
      >
        Next
      </button>
    </div>
  );
}
