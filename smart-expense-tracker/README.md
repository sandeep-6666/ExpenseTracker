# Smart Expense Tracker with AI Insights — Backend

Spring Boot 3 / Java 21 backend for the Cognizant GenC Next interview-prep project.

## Prerequisites
- Java 21 (JDK)
- Maven 3.8+ (or use the IDE's bundled Maven)
- MySQL 8 running locally
- VS Code with the **Extension Pack for Java** + **Spring Boot Extension Pack** (or IntelliJ)

## 1. Configure the database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```
The database `expense_tracker_db` will be auto-created on first run (`createDatabaseIfNotExist=true`), and tables are auto-created by Hibernate (`ddl-auto=update`). You can also run `database_setup.sql` manually if you prefer explicit control.

## 2. Run the app

**From terminal:**
```bash
mvn spring-boot:run
```

**From VS Code:**
1. Open this folder in VS Code.
2. Let the Java extensions index the project (first load takes a minute).
3. Open `ExpenseTrackerApplication.java` → click **Run** above `main()`, or use the Spring Boot dashboard (bug/play icon in the sidebar).

The app starts on **http://localhost:8080**.

## 3. Explore the API
Swagger UI: **http://localhost:8080/swagger-ui.html**

## 4. Try it end-to-end
1. `POST /api/auth/register` → create a user, get a JWT back.
2. Click **Authorize** in Swagger and paste `Bearer <token>` (or add header `Authorization: Bearer <token>` in Postman).
3. `POST /api/expenses`, `POST /api/incomes`, `POST /api/budgets` to add data.
4. `GET /api/dashboard` for totals + chart data.
5. `GET /api/insights` for AI-generated (rule-based) spending insights.

## What's implemented in this backend build
- JWT auth (register/login), BCrypt password hashing, role field (USER/ADMIN)
- Expense CRUD + search + filter by category/date/month/amount range
- Income CRUD
- Budget: set monthly limit, get status (spent, remaining, % used, 80% warning flag)
- Dashboard: total income/expense/balance, category-wise spending, 6-month trend
- AI Insights: rule-based engine (month-over-month category increases, high medical spend, entertainment trend, food-spend saving tip) — built behind an `AiInsightService` interface so a Gemini/OpenAI-backed implementation can be swapped in later without touching the controller
- Global exception handling with consistent JSON error shape
- Swagger/OpenAPI docs with JWT bearer auth wired in
- SLF4J logging throughout the service layer

## Not yet included (next phases, per your original plan)
- React frontend (Phase 11)
- Postman collection export (Phase 12)
- Deployment config (Phase 13)

Ask me to continue with any of these and I'll build them the same way.
