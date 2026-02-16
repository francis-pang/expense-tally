import { useState, useEffect, useRef } from 'react';
import { ChevronDownIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import { getCategories } from '@/services/api';
import type { Category } from '@/types';

interface CategoryPickerProps {
  value: string | null;
  onChange: (categoryId: string | null) => void;
  suggestedCategoryId?: string | null;
  placeholder?: string;
  disabled?: boolean;
}

export function CategoryPicker({
  value,
  onChange,
  suggestedCategoryId,
  placeholder = 'Select category',
  disabled = false,
}: CategoryPickerProps) {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    getCategories()
      .then((res) => setCategories(res.data))
      .catch(() => setCategories([]))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const getCategoryPath = (cat: Category): string => {
    if (!cat.parentId) return cat.name;
    const parent = categories.find((c) => c.id === cat.parentId);
    if (!parent) return cat.name;
    return `${getCategoryPath(parent)} > ${cat.name}`;
  };

  const filteredCategories = categories.filter((cat) => {
    const path = getCategoryPath(cat).toLowerCase();
    return path.includes(search.toLowerCase());
  });

  const selectedCategory = categories.find((c) => c.id === value);

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        onClick={() => !disabled && setIsOpen(!isOpen)}
        disabled={disabled}
        className="flex w-full items-center justify-between rounded-lg border border-slate-300 bg-white px-3 py-2 text-left text-sm shadow-sm transition-colors hover:border-slate-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500 disabled:cursor-not-allowed disabled:opacity-50"
      >
        <span className={value ? 'text-slate-900' : 'text-slate-500'}>
          {value ? getCategoryPath(selectedCategory!) : placeholder}
        </span>
        <ChevronDownIcon
          className={`h-4 w-4 text-slate-400 transition-transform ${isOpen ? 'rotate-180' : ''}`}
        />
      </button>

      {isOpen && (
        <div className="absolute left-0 right-0 top-full z-50 mt-1 max-h-60 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-lg">
          <div className="border-b border-slate-200 p-2">
            <div className="relative">
              <MagnifyingGlassIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search categories..."
                className="w-full rounded-md border border-slate-300 py-2 pl-9 pr-3 text-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              />
            </div>
          </div>
          <div className="max-h-44 overflow-y-auto scrollbar-thin">
            {loading ? (
              <div className="p-4 text-center text-sm text-slate-500">
                Loading...
              </div>
            ) : (
              <>
                <button
                  type="button"
                  onClick={() => {
                    onChange(null);
                    setIsOpen(false);
                  }}
                  className={`w-full px-3 py-2 text-left text-sm hover:bg-slate-50 ${
                    !value ? 'bg-primary-50 text-primary-700' : 'text-slate-700'
                  }`}
                >
                  None
                </button>
                {filteredCategories.map((cat) => {
                  const path = getCategoryPath(cat);
                  const isSuggested = cat.id === suggestedCategoryId;
                  const isSelected = cat.id === value;
                  return (
                    <button
                      key={cat.id}
                      type="button"
                      onClick={() => {
                        onChange(cat.id);
                        setIsOpen(false);
                      }}
                      className={`flex w-full items-center justify-between px-3 py-2 text-left text-sm hover:bg-slate-50 ${
                        isSelected ? 'bg-primary-50 text-primary-700' : 'text-slate-700'
                      } ${isSuggested ? 'border-l-2 border-primary-500' : ''}`}
                    >
                      <span>{path}</span>
                      {isSuggested && !isSelected && (
                        <span className="rounded bg-primary-100 px-2 py-0.5 text-xs text-primary-700">
                          Suggested
                        </span>
                      )}
                    </button>
                  );
                })}
                {filteredCategories.length === 0 && (
                  <div className="p-4 text-center text-sm text-slate-500">
                    No categories found
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
