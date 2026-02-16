# Frontend Architecture & UI Guidelines

The frontend is a React 18 single-page application built with TypeScript, Vite 5, and Tailwind CSS 3.4. It uses AWS Amplify for Cognito authentication and Axios for API communication.

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| React | ^18.2.0 | UI framework |
| TypeScript | ^5.3.0 | Type safety |
| Vite | ^5.0.0 | Build tool and dev server |
| Tailwind CSS | ^3.4.0 | Utility-first CSS |
| React Router | ^6.21.0 | Client-side routing |
| AWS Amplify | ^6.0.0 | Cognito auth integration |
| Axios | ^1.6.0 | HTTP client |
| Recharts | ^2.10.0 | Charts and visualizations |
| Heroicons | ^2.1.1 | SVG icon library |
| date-fns | ^3.0.0 | Date formatting |

## Directory Structure

```
frontend/
├── public/
│   └── vite.svg                  # Favicon
├── src/
│   ├── main.tsx                  # App entry point (Amplify config + render)
│   ├── App.tsx                   # Router + AuthGuard + route definitions
│   ├── index.css                 # Tailwind directives + global styles
│   ├── config/
│   │   └── amplify.ts            # AWS Amplify / Cognito configuration
│   ├── hooks/
│   │   └── useAuth.ts            # Authentication hook (sign in/out, token)
│   ├── services/
│   │   └── api.ts                # Axios client with auth interceptor
│   ├── types/
│   │   └── index.ts              # TypeScript interfaces
│   ├── components/
│   │   ├── Layout.tsx            # App shell (header, sidebar, bottom nav)
│   │   ├── CategoryPicker.tsx    # Searchable category dropdown
│   │   ├── TransactionRow.tsx    # Transaction list item with actions
│   │   └── SpendChart.tsx        # Pie/bar/donut chart component
│   └── pages/
│       ├── Dashboard.tsx         # Spending overview and charts
│       ├── Transactions.tsx      # Transaction list with filters
│       ├── ReviewQueue.tsx       # Unconfirmed transaction review
│       ├── Categories.tsx        # Category CRUD management
│       ├── ManualEntry.tsx       # Manual expense entry form
│       └── Login.tsx             # Login / redirect to Cognito
├── index.html                    # HTML entry point
├── package.json
├── vite.config.ts                # Vite config (alias, proxy)
├── tsconfig.json                 # TypeScript config
├── tailwind.config.js            # Tailwind theme customization
├── postcss.config.js             # PostCSS (Tailwind + Autoprefixer)
└── .env.example                  # Environment variable template
```

## Component Architecture

### Component Tree

```
main.tsx
  └── Amplify.configure()
      └── App (BrowserRouter)
          └── AppRoutes (Routes)
              ├── /login → Login
              └── / → AuthGuard
                  └── Layout
                      ├── Header (logo, username, sign out)
                      ├── Sidebar (desktop, lg+)
                      ├── Bottom Nav (mobile, <lg)
                      ├── Slide-out Menu (mobile hamburger)
                      └── <Outlet />
                          ├── index → Dashboard
                          │   ├── SpendChart (pie - by category)
                          │   ├── SpendChart (bar - monthly trend)
                          │   ├── SpendChart (donut - payment method)
                          │   └── TransactionRow[] (recent)
                          ├── /transactions → Transactions
                          │   └── TransactionRow[] + CategoryPicker
                          ├── /review → ReviewQueue
                          │   └── TransactionRow[] (unconfirmed)
                          ├── /categories → Categories
                          └── /add → ManualEntry
```

### Component Responsibilities

| Component | Type | Description |
|-----------|------|-------------|
| `App` | Router | Defines routes, wraps authenticated routes with `AuthGuard` |
| `AuthGuard` | Guard | Shows loading spinner during auth check, redirects to `/login` if unauthenticated |
| `Layout` | Shell | Provides app chrome: sticky header, desktop sidebar, mobile bottom nav, mobile slide-out |
| `Dashboard` | Page | Fetches dashboard data + categories, renders charts and recent transactions |
| `Transactions` | Page | Full transaction list with year/date/category filters |
| `ReviewQueue` | Page | Lists unconfirmed transactions for review and confirmation |
| `Categories` | Page | Category CRUD with parent-child hierarchy |
| `ManualEntry` | Page | Form for adding manual cash/check expenses |
| `Login` | Page | Initiates Cognito Hosted UI redirect |
| `SpendChart` | Shared | Renders pie, bar, or donut chart via Recharts |
| `TransactionRow` | Shared | Displays a single transaction with category picker and confirm action |
| `CategoryPicker` | Shared | Searchable dropdown for selecting a category |

## Routing

| Path | Component | Auth Required | Description |
|------|-----------|---------------|-------------|
| `/login` | `Login` | No | Cognito sign-in redirect |
| `/` | `Dashboard` | Yes | Spending overview |
| `/transactions` | `Transactions` | Yes | All transactions |
| `/review` | `ReviewQueue` | Yes | Unconfirmed transactions |
| `/categories` | `Categories` | Yes | Category management |
| `/add` | `ManualEntry` | Yes | Add manual expense |
| `*` | Redirect to `/` | — | Catch-all redirect |

All authenticated routes are children of `Layout`, which uses React Router's `<Outlet />` for nested rendering.

## State Management

The frontend uses **local component state** exclusively -- no global state library (Redux, Zustand, etc.):

- **Auth state**: Managed by `useAuth()` hook (user, loading, sign in/out)
- **Page data**: Each page fetches its own data via `useEffect` + `useState`
- **Shared data**: Categories are fetched independently by each page that needs them
- **Refresh pattern**: Callback props (e.g., `onUpdate`) trigger re-fetches in parent

This approach is appropriate for the current scale. If the application grows, consider introducing a lightweight state management solution.

## API Client Layer

The API service (`services/api.ts`) provides a typed Axios client:

### Configuration
- **Base URL**: `VITE_API_URL` environment variable, defaults to `/api` (proxied in dev, CloudFront in prod)
- **Content-Type**: `application/json`

### Interceptors

**Request interceptor** -- Adds Cognito ID token:
```
Every request → fetchAuthSession() → Add "Authorization: Bearer <idToken>"
```

**Response interceptor** -- Handles 401:
```
401 response → Redirect to /login
```

### API Functions

| Domain | Functions |
|--------|-----------|
| Health | `getHealth()` |
| Categories | `getCategories`, `createCategory`, `updateCategory`, `deleteCategory` |
| Transactions | `getTransactions`, `createTransaction`, `updateTransaction`, `deleteTransaction`, `getUnconfirmedTransactions`, `confirmTransaction` |
| Dashboard | `getDashboard(year?, month?)` |
| Sync | `triggerSync`, `getSyncLogs` |
| Connections | `getConnections`, `createTellerConnection` |

## Authentication Flow

```
┌──────────┐     ┌──────────┐     ┌───────────────┐     ┌────────────┐
│  Browser  │────▶│ /login   │────▶│ Cognito       │────▶│ Callback   │
│           │     │ page     │     │ Hosted UI     │     │ /          │
└──────────┘     └──────────┘     │ (email+pass)  │     └──────┬─────┘
                                  └───────────────┘            │
                                                               ▼
                                                  ┌────────────────────┐
                                                  │ useAuth() checks   │
                                                  │ getCurrentUser()   │
                                                  │ → isAuthenticated  │
                                                  └────────────────────┘
```

1. `AuthGuard` calls `useAuth()` which runs `getCurrentUser()` on mount
2. If no user found, redirects to `/login`
3. `Login` page calls `signInWithRedirect()` which opens Cognito Hosted UI
4. After successful auth, Cognito redirects back with authorization code
5. Amplify exchanges code for tokens (ID, access, refresh)
6. Subsequent API calls attach the ID token via request interceptor
7. On sign out, Amplify clears session and redirects to `/login`

## UI Guidelines

### Design System

The application follows a clean, modern design language:

- **Color palette**: Indigo (`primary`) for brand/actions, Slate for neutral/text
- **Typography**: System font stack, with clear size hierarchy (text-xs through text-4xl)
- **Spacing**: Consistent spacing scale via Tailwind (gap-2, p-4, p-6, etc.)
- **Border radius**: Rounded corners (`rounded-lg`, `rounded-xl`) for cards and inputs
- **Shadows**: Subtle shadows (`shadow-sm`) for card elevation
- **Icons**: Heroicons outline (inactive) and solid (active) variants

### Layout Patterns

| Viewport | Navigation | Content |
|----------|-----------|---------|
| Desktop (lg+) | Fixed left sidebar (56px/w-56) | Fluid main area with p-8 |
| Tablet/Mobile (<lg) | Bottom navigation bar + hamburger slide-out | Fluid main area with p-4, pb-20 for bottom nav clearance |

### Component Patterns

**Cards**: White background, slate-200 border, rounded-xl, p-6, shadow-sm
```html
<div class="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
```

**Form inputs**: Rounded-lg, slate-300 border, focus ring with primary color
```html
<input class="rounded-lg border border-slate-300 px-3 py-2 text-sm
  focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500" />
```

**Buttons (primary)**: Primary-600 background, white text, hover darkens
```html
<button class="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white
  hover:bg-primary-700 transition-colors">
```

**Loading states**: Skeleton placeholders with `animate-pulse` on slate-200 backgrounds

**Empty states**: Centered icon (slate-300) + descriptive text + action hint

### Navigation Icons

| Route | Inactive Icon | Active Icon |
|-------|--------------|-------------|
| Dashboard | `HomeIcon` (outline) | `HomeIcon` (solid) |
| Transactions | `BanknotesIcon` (outline) | `BanknotesIcon` (solid) |
| Review | `ClipboardDocumentCheckIcon` (outline) | `ClipboardDocumentCheckIcon` (solid) |
| Categories | `FolderIcon` (outline) | `FolderIcon` (solid) |
| Add Expense | `PlusCircleIcon` (outline) | `PlusCircleIcon` (solid) |

### Responsive Breakpoints

| Breakpoint | Min Width | Usage |
|-----------|-----------|-------|
| Default | 0px | Mobile-first base styles |
| `sm` | 640px | Expanded form layouts, visible username |
| `md` | 768px | 2-column chart grids |
| `lg` | 1024px | Desktop sidebar visible, 2-3 column grids |

## User Flow Diagrams

### Transaction Review Flow

```
User opens Review Queue
    │
    ▼
Fetch unconfirmed transactions (GET /api/transactions/unconfirmed)
    │
    ▼
Display list of TransactionRow components
    │
    ├── User selects a category (CategoryPicker)
    │       └── PUT /api/transactions/{id} with categoryId
    │           └── KeywordService learns from tagging
    │
    └── User clicks "Confirm"
            └── PUT /api/transactions/{id}/confirm
                └── Transaction marked confirmed, removed from unconfirmed index
```

### Manual Entry Flow

```
User opens Add Expense (/add)
    │
    ▼
Fill form: date, amount, currency, description, merchant, payment method
    │
    ▼
Optionally select category (CategoryPicker)
    │
    ▼
Submit → POST /api/transactions
    │
    ▼
Redirect to Transactions page or show success
```

### Dashboard Flow

```
User opens Dashboard (/)
    │
    ▼
Parallel fetch: getDashboard(year, month) + getCategories()
    │
    ▼
Render:
├── Total Spend card
├── Spend by Category (pie chart)
├── Monthly Trend (bar chart)
├── Payment Method (donut chart)
└── Recent Transactions list

User changes year/month filter → re-fetch dashboard data
```

## Build Configuration

### Vite (`vite.config.ts`)
- **Plugin**: `@vitejs/plugin-react` for JSX/TSX
- **Path alias**: `@` maps to `./src` (enables `@/components/Layout` imports)
- **Dev proxy**: `/api` proxied to `http://localhost:3000` (SAM local)

### TypeScript (`tsconfig.json`)
- **Target**: ES2020
- **Strict mode**: Enabled
- **Path mapping**: `@/*` resolves to `src/*`

### Tailwind (`tailwind.config.js`)
- **Custom colors**: `primary` mapped to Tailwind indigo, `slate` for neutrals
- **Content paths**: `./index.html`, `./src/**/*.{js,ts,jsx,tsx}`

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `VITE_API_URL` | No | API base URL (defaults to `/api`) |
| `VITE_COGNITO_USER_POOL_ID` | Yes | Cognito User Pool ID |
| `VITE_COGNITO_CLIENT_ID` | Yes | Cognito App Client ID |
| `VITE_COGNITO_DOMAIN` | Yes | Cognito Hosted UI domain |
| `VITE_REDIRECT_URL` | Yes | OAuth redirect URL (e.g., `http://localhost:5173`) |

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [API Reference](API.md)
- [Security](SECURITY.md)
- [Developer Guide](DEVELOPER-GUIDE.md)
