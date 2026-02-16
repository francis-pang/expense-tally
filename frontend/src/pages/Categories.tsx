import { useState, useEffect } from 'react';
import {
  PlusIcon,
  PencilIcon,
  TrashIcon,
  ChevronDownIcon,
  ChevronRightIcon,
} from '@heroicons/react/24/outline';
import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} from '@/services/api';
import type { Category } from '@/types';

export function Categories() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [newName, setNewName] = useState('');
  const [newParentId, setNewParentId] = useState<string>('');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editName, setEditName] = useState('');
  const [deleteConfirmId, setDeleteConfirmId] = useState<string | null>(null);
  const [expandedIds, setExpandedIds] = useState<Set<string>>(new Set());

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const res = await getCategories();
      setCategories(res.data);
    } catch {
      setCategories([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    if (categories.length > 0) {
      const parentIds = [...new Set(categories.filter((c) => c.parentId).map((c) => c.parentId!))];
      setExpandedIds(new Set(parentIds));
    }
  }, [categories]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName.trim()) return;
    try {
      await createCategory({
        name: newName.trim(),
        parentId: newParentId || undefined,
      });
      setNewName('');
      setNewParentId('');
      fetchCategories();
    } catch {
      // Error - could add toast
    }
  };

  const handleUpdate = async (id: string) => {
    if (!editName.trim()) return;
    try {
      await updateCategory(id, { name: editName.trim() });
      setEditingId(null);
      setEditName('');
      fetchCategories();
    } catch {
      // Error
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteCategory(id);
      setDeleteConfirmId(null);
      fetchCategories();
    } catch {
      // Error
    }
  };

  const getChildren = (parentId: string | null) =>
    categories.filter((c) => c.parentId === parentId);

  const toggleExpand = (id: string) => {
    setExpandedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const renderCategory = (cat: Category, depth: number) => {
    const children = getChildren(cat.id);
    const hasChildren = children.length > 0;
    const isExpanded = expandedIds.has(cat.id);
    const isEditing = editingId === cat.id;
    const isDeleteConfirm = deleteConfirmId === cat.id;

    return (
      <div key={cat.id} className="ml-4">
        <div
          className="flex items-center gap-2 rounded-lg py-2 hover:bg-slate-50"
          style={{ paddingLeft: `${depth * 16}px` }}
        >
          <button
            type="button"
            onClick={() => hasChildren && toggleExpand(cat.id)}
            className="flex w-6 items-center justify-center text-slate-400"
          >
            {hasChildren ? (
              isExpanded ? (
                <ChevronDownIcon className="h-4 w-4" />
              ) : (
                <ChevronRightIcon className="h-4 w-4" />
              )
            ) : null}
          </button>
          {isEditing ? (
            <div className="flex flex-1 items-center gap-2">
              <input
                type="text"
                value={editName}
                onChange={(e) => setEditName(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleUpdate(cat.id);
                  if (e.key === 'Escape') {
                    setEditingId(null);
                    setEditName('');
                  }
                }}
                className="flex-1 rounded border border-slate-300 px-2 py-1 text-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
                autoFocus
              />
              <button
                type="button"
                onClick={() => handleUpdate(cat.id)}
                className="rounded bg-primary-600 px-2 py-1 text-sm text-white hover:bg-primary-700"
              >
                Save
              </button>
              <button
                type="button"
                onClick={() => {
                  setEditingId(null);
                  setEditName('');
                }}
                className="rounded bg-slate-200 px-2 py-1 text-sm hover:bg-slate-300"
              >
                Cancel
              </button>
            </div>
          ) : isDeleteConfirm ? (
            <div className="flex flex-1 items-center gap-2">
              <span className="text-sm text-slate-600">
                Delete &quot;{cat.name}&quot;?
              </span>
              <button
                type="button"
                onClick={() => handleDelete(cat.id)}
                className="rounded bg-red-600 px-2 py-1 text-sm text-white hover:bg-red-700"
              >
                Yes
              </button>
              <button
                type="button"
                onClick={() => setDeleteConfirmId(null)}
                className="rounded bg-slate-200 px-2 py-1 text-sm hover:bg-slate-300"
              >
                No
              </button>
            </div>
          ) : (
            <>
              <span className="flex-1 font-medium text-slate-900">
                {cat.name}
              </span>
              <button
                type="button"
                onClick={() => {
                  setEditingId(cat.id);
                  setEditName(cat.name);
                }}
                className="rounded p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
              >
                <PencilIcon className="h-4 w-4" />
              </button>
              <button
                type="button"
                onClick={() => setDeleteConfirmId(cat.id)}
                className="rounded p-1 text-slate-400 hover:bg-red-50 hover:text-red-600"
              >
                <TrashIcon className="h-4 w-4" />
              </button>
            </>
          )}
        </div>
        {hasChildren && isExpanded && (
          <div className="border-l border-slate-200 pl-2">
            {children.map((child) => renderCategory(child, depth + 1))}
          </div>
        )}
      </div>
    );
  };

  const rootCategories = getChildren(null);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Categories</h1>

      {/* Add category form */}
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <form onSubmit={handleCreate} className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <div className="flex-1">
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Category Name
            </label>
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              placeholder="e.g. Groceries"
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            />
          </div>
          <div className="sm:w-48">
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Parent (optional)
            </label>
            <select
              value={newParentId}
              onChange={(e) => setNewParentId(e.target.value)}
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            >
              <option value="">None</option>
              {categories
                .filter((c) => !c.parentId)
                .map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
            </select>
          </div>
          <button
            type="submit"
            className="flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700"
          >
            <PlusIcon className="h-5 w-5" />
            Add Category
          </button>
        </form>
      </div>

      {/* Category tree */}
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <h2 className="mb-4 text-sm font-semibold text-slate-700">
          Category Tree
        </h2>
        {loading ? (
          <div className="space-y-2">
            {[1, 2, 3, 4, 5].map((i) => (
              <div key={i} className="h-10 animate-pulse rounded bg-slate-200" />
            ))}
          </div>
        ) : rootCategories.length > 0 ? (
          rootCategories.map((cat) => renderCategory(cat, 0))
        ) : (
          <p className="py-8 text-center text-sm text-slate-500">
            No categories yet. Add one above to get started.
          </p>
        )}
      </div>
    </div>
  );
}
