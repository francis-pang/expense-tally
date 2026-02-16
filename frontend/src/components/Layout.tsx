import { NavLink, Outlet } from 'react-router-dom';
import {
  HomeIcon,
  BanknotesIcon,
  ClipboardDocumentCheckIcon,
  FolderIcon,
  PlusCircleIcon,
  BuildingLibraryIcon,
  ArrowRightOnRectangleIcon,
  Bars3Icon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import {
  HomeIcon as HomeIconSolid,
  BanknotesIcon as BanknotesIconSolid,
  ClipboardDocumentCheckIcon as ClipboardDocumentCheckIconSolid,
  FolderIcon as FolderIconSolid,
  PlusCircleIcon as PlusCircleIconSolid,
  BuildingLibraryIcon as BuildingLibraryIconSolid,
} from '@heroicons/react/24/solid';
import { useState } from 'react';
import { useAuth } from '@/hooks/useAuth';

const navItems = [
  { to: '/', icon: HomeIcon, iconActive: HomeIconSolid, label: 'Dashboard' },
  {
    to: '/transactions',
    icon: BanknotesIcon,
    iconActive: BanknotesIconSolid,
    label: 'Transactions',
  },
  {
    to: '/review',
    icon: ClipboardDocumentCheckIcon,
    iconActive: ClipboardDocumentCheckIconSolid,
    label: 'Review',
  },
  {
    to: '/categories',
    icon: FolderIcon,
    iconActive: FolderIconSolid,
    label: 'Categories',
  },
  {
    to: '/connections',
    icon: BuildingLibraryIcon,
    iconActive: BuildingLibraryIconSolid,
    label: 'Banks',
  },
  {
    to: '/add',
    icon: PlusCircleIcon,
    iconActive: PlusCircleIconSolid,
    label: 'Add Expense',
  },
];

export function Layout() {
  const { user, signOut } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Top header */}
      <header className="sticky top-0 z-40 border-b border-slate-200 bg-white shadow-sm">
        <div className="flex h-14 items-center justify-between px-4 sm:px-6">
          <div className="flex items-center gap-4">
            <button
              type="button"
              className="lg:hidden rounded-lg p-2 text-slate-600 hover:bg-slate-100"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              {mobileMenuOpen ? (
                <XMarkIcon className="h-6 w-6" />
              ) : (
                <Bars3Icon className="h-6 w-6" />
              )}
            </button>
            <h1 className="text-xl font-semibold text-primary-600">
              Expense Tally
            </h1>
          </div>
          <div className="flex items-center gap-2">
            <span className="hidden text-sm text-slate-600 sm:inline">
              {user?.username}
            </span>
            <button
              type="button"
              onClick={signOut}
              className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-600 transition-colors hover:bg-slate-100 hover:text-slate-900"
            >
              <ArrowRightOnRectangleIcon className="h-5 w-5" />
              <span className="hidden sm:inline">Sign out</span>
            </button>
          </div>
        </div>
      </header>

      <div className="flex">
        {/* Desktop sidebar */}
        <aside className="hidden w-56 flex-shrink-0 border-r border-slate-200 bg-white lg:block">
          <nav className="sticky top-14 flex flex-col gap-1 p-4">
            {navItems.map(({ to, icon: Icon, iconActive: IconActive, label }) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/'}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-primary-50 text-primary-700'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                {({ isActive }) =>
                  isActive ? (
                    <IconActive className="h-5 w-5" />
                  ) : (
                    <Icon className="h-5 w-5" />
                  )
                }
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>

        {/* Mobile bottom nav */}
        <nav className="fixed bottom-0 left-0 right-0 z-50 flex items-center justify-around border-t border-slate-200 bg-white py-2 lg:hidden">
          {navItems.map(({ to, icon: Icon, iconActive: IconActive, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              onClick={() => setMobileMenuOpen(false)}
              className={({ isActive }) =>
                `flex flex-col items-center gap-1 rounded-lg px-4 py-2 text-xs font-medium transition-colors ${
                  isActive
                    ? 'text-primary-600'
                    : 'text-slate-500 hover:text-slate-700'
                }`
              }
            >
              {({ isActive }) =>
                isActive ? (
                  <IconActive className="h-6 w-6" />
                ) : (
                  <Icon className="h-6 w-6" />
                )
              }
              {label}
            </NavLink>
          ))}
        </nav>

        {/* Mobile slide-out menu */}
        {mobileMenuOpen && (
          <div
            className="fixed inset-0 z-30 bg-black/20 lg:hidden"
            onClick={() => setMobileMenuOpen(false)}
          />
        )}
        <aside
          className={`fixed left-0 top-14 z-40 h-[calc(100vh-3.5rem)] w-56 transform border-r border-slate-200 bg-white transition-transform lg:hidden ${
            mobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
          }`}
        >
          <nav className="flex flex-col gap-1 p-4">
            {navItems.map(({ to, icon: Icon, iconActive: IconActive, label }) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/'}
                onClick={() => setMobileMenuOpen(false)}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-primary-50 text-primary-700'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                {({ isActive }) =>
                  isActive ? (
                    <IconActive className="h-5 w-5" />
                  ) : (
                    <Icon className="h-5 w-5" />
                  )
                }
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>

        {/* Main content */}
        <main className="flex-1 pb-20 lg:pb-8">
          <div className="p-4 sm:p-6 lg:p-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
