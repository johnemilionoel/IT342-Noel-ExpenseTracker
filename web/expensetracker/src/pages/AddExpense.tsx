import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { addExpense, getCategories } from "../services/api"

export default function AddExpense() {
  const user = JSON.parse(localStorage.getItem("user") || "{}")
  const navigate = useNavigate()
  const [categories, setCategories] = useState<any[]>([])
  const [form, setForm] = useState({ amount: "", categoryId: "", date: new Date().toISOString().split("T")[0], description: "" })
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    getCategories().then(res => setCategories(res.data)).catch(console.error)
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    if (!form.amount || Number(form.amount) <= 0) { setError("Amount must be greater than 0"); return }
    if (!form.categoryId) { setError("Please select a category"); return }
    if (!form.date) { setError("Please select a date"); return }

    setLoading(true)
    try {
      await addExpense(user.id, {
        amount: Number(form.amount),
        categoryId: Number(form.categoryId),
        date: form.date,
        description: form.description,
      })
      navigate("/expenses")
    } catch (err: any) {
      setError(err.response?.data?.error || "Failed to add expense")
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-800 mb-2">Add New Expense</h1>
      <p className="text-sm text-gray-500 mb-8">Fill in the details below to record a new expense.</p>

      {error && (
        <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg text-sm mb-6">{error}</div>
      )}

      <form onSubmit={handleSubmit} className="bg-white rounded-xl border border-gray-100 shadow-sm p-8 space-y-6">
        <div className="grid grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Amount (₱)</label>
            <input type="number" step="0.01" min="0" placeholder="0.00" value={form.amount}
              onChange={e => setForm({ ...form, amount: e.target.value })}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
            <select value={form.categoryId} onChange={e => setForm({ ...form, categoryId: e.target.value })}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option value="">Select category</option>
              {categories.map((c: any) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
          <input type="date" value={form.date} onChange={e => setForm({ ...form, date: e.target.value })}
            className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Description (optional)</label>
          <textarea rows={3} placeholder="Provide a brief description..." value={form.description}
            onChange={e => setForm({ ...form, description: e.target.value })}
            className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none" />
        </div>

        <div className="flex gap-4 pt-2">
          <button type="submit" disabled={loading}
            className="flex-1 bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-50">
            {loading ? "Saving..." : "Save Expense"}
          </button>
          <button type="button" onClick={() => navigate("/expenses")}
            className="flex-1 border border-gray-200 text-gray-600 py-3 rounded-lg font-medium hover:bg-gray-50 transition-colors">
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}
