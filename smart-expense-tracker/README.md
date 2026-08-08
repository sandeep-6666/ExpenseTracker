# Smart Expense Tracker with AI Insights

A full-stack personal finance app built as a Cognizant GenC Next interview-prep project: track expenses and income, set budgets, view spending analytics, and get rule-based AI insights — all behind JWT-secured REST APIs.

**Stack:** Spring Boot 3 (Java 21) · MySQL 8 · React 19 (Vite) · Tailwind CSS v4 · Chart.js

```
smart-expense-tracker/
├── src/                # Spring Boot backend
├── frontend/           # React (Vite) frontend
├── postman/            # Postman collection
└── database_setup.sql
```

---

## Backend

### Tech Stack
- Java 21, Spring Boot 3
- Spring Web, Spring Data JPA (Hibernate), Spring Security
- MySQL 8
- JWT auth (`jjwt`), BCrypt password hashing
- Lombok
- Springdoc OpenAPI (Swagger UI)

### Prerequisites
- Java 21 (JDK)
- Maven 3.8+ (or use the IDE's bundled Maven)
- MySQL 8 running locally (e.g. via XAMPP)

### 1. Configure the database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```
The database `expense_tracker_db` will be auto-created on first run (`createDatabaseIfNotExist=true`), and tables are auto-created by Hibernate (`ddl-auto=update`). You can also run `database_setup.sql` manually if you prefer explicit control.

Before deploying anywhere beyond localhost, also replace `app.jwt.secret` in `application.properties` with your own generated secret — the committed value is a placeholder for local dev only.

### 2. Run the app
```bash
mvn spring-boot:run
```
Or from your IDE: open `ExpenseTrackerApplication.java` → **Run**.

The app starts on **http://localhost:8080**.

### 3. Explore the API
- Swagger UI: **http://localhost:8080/swagger-ui.html**
- OpenAPI JSON: **http://localhost:8080/v3/api-docs**
- Postman collection: `postman/Smart-Expense-Tracker.postman_collection.json`

### API Reference

#### Auth — `/api/auth`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Create a user, returns JWT |
| POST | `/login` | Authenticate, returns JWT |

#### Users — `/api/users`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/me` | Get current user profile |
| PUT | `/me` | Update current user profile |
| PUT | `/me/password` | Change password |

#### Expenses — `/api/expenses`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create an expense |
| GET | `/` | List all expenses |
| GET | `/{id}` | Get one expense |
| PUT | `/{id}` | Update an expense |
| DELETE | `/{id}` | Delete an expense |
| GET | `/search` | Keyword search |
| GET | `/filter/category` | Filter by category |
| GET | `/filter/date` | Filter by date range |
| GET | `/filter/month` | Filter by month/year |
| GET | `/filter/amount` | Filter by amount range |

#### Income — `/api/incomes`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create an income entry |
| GET | `/` | List all income entries |
| GET | `/{id}` | Get one income entry |
| PUT | `/{id}` | Update an income entry |
| DELETE | `/{id}` | Delete an income entry |

#### Budget — `/api/budgets`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Set a monthly budget limit |
| GET | `/status` | Spent, remaining, % used, 80% warning flag (by month/year) |

#### Dashboard — `/api/dashboard`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Total income/expense/balance, category-wise spending, 6-month trend |

#### Insights — `/api/insights`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Rule-based AI spending insights |

All endpoints except `/api/auth/**` and Swagger paths require `Authorization: Bearer <token>`.

### Backend features
- JWT auth (register/login), BCrypt password hashing, role field (USER/ADMIN)
- User profile: view/update profile, change password
- Expense CRUD + search + filter by category/date/month/amount range
- Income CRUD
- Budget: set monthly limit, get status (spent, remaining, % used, 80% warning flag)
- Dashboard: total income/expense/balance, category-wise spending, 6-month trend
- AI Insights: rule-based engine (month-over-month category increases, high medical spend, entertainment trend, food-spend saving tip), built behind an `AiInsightService` interface so a different implementation can be swapped in later without touching the controller
- Global exception handling with consistent JSON error shape
- Swagger/OpenAPI docs with JWT bearer auth wired in
- SLF4J logging throughout the service layer

### Backend structure
```
src/main/java/com/sandeep/expensetracker/
├── config/          # Security & OpenAPI config
├── controller/      # REST controllers
├── dto/             # Request/response DTOs
├── entity/          # JPA entities
├── exception/       # Global exception handling
├── jwt/             # JWT filter & util
├── mapper/          # Entity ↔ DTO mappers
├── repository/      # Spring Data JPA repositories
├── response/        # Common ApiResponse wrapper
├── security/        # UserDetails implementations
├── service/         # Service interfaces
├── serviceImpl/      # Service implementations
└── util/            # Constants
```

---

## Frontend

### Tech Stack
- React 19 + Vite
- Tailwind CSS v4
- React Router v7
- Axios (with interceptors for auth + error toasts)
- React Hook Form
- Chart.js / react-chartjs-2
- React Toastify

### 1. Configure the API URL
`frontend/.env`:
```
VITE_API_BASE_URL=http://localhost:8080
```

### 2. Install & run
```bash
cd frontend
npm install
npm run dev
```
The app starts on Vite's default dev port (check terminal output, typically **http://localhost:5173**). Make sure the backend is running on port 8080 first.

Other scripts:
```bash
npm run build     # production build → frontend/dist
npm run preview    # preview the production build locally
npm run lint       # oxlint
```

### Pages / Routes
| Route | Page | Notes |
|---|---|---|
| `/login` | Login | Public |
| `/register` | Register | Public |
| `/dashboard` | Dashboard | Totals, charts, protected |
| `/expenses` | Expenses | CRUD + search/filter, protected |
| `/income` | Income | CRUD, protected |
| `/budgets` | Budgets | Set limit + status, protected |
| `/analytics` | Analytics | Charts/insights, protected |
| `/profile` | Profile | View/update profile, protected |
| `/settings` | Settings | Change password, theme, protected |
| `*` | NotFound | 404 |

Protected routes are gated by `ProtectedRoute` + `AuthContext`; unauthenticated users are redirected to `/login`, and authenticated users hitting `/login` or `/register` are redirected to `/dashboard`.

### Frontend structure
```
frontend/src/
├── assets/          # Images/icons
├── components/      # LoadingSpinner, Modal, Pagination, ProtectedRoute, StatCard
├── context/         # AuthContext, ThemeContext
├── hooks/           # usePagination
├── layouts/         # DashboardLayout, AuthLayout, Navbar, Sidebar
├── pages/           # Route-level pages (see table above)
├── routes/          # AppRoutes
├── services/        # api.js (axios instance + interceptors), index.js (per-resource services)
└── utils/           # constants, formatters, storage (localStorage helpers)
```

### Notes on the API layer
- `services/api.js` sets up a single Axios instance, attaches the JWT bearer token to every request, and centrally handles 401/403 (clears auth, redirects to `/login`) and other errors (toast notification), with an opt-out `_silent` flag for calls that shouldn't toast on failure.
- `services/index.js` exposes typed-ish service objects (`authService`, `expenseService`, `incomeService`, `budgetService`, `dashboardService`, `insightService`, `userService`) that wrap the Axios calls per backend resource.

---

## Running the full stack locally
1. Start MySQL (e.g. via XAMPP).
2. `mvn spring-boot:run` from the project root — backend on `:8080`.
3. `cd frontend && npm install && npm run dev` — frontend on Vite's dev port.
4. Open the frontend, register a user, and use the app end-to-end (register → login → add expenses/income → set a budget → check dashboard/analytics/insights).
