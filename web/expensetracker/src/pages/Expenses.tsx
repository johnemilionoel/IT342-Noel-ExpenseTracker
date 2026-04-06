import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { getExpenses, deleteExpense } from "../services/api"

export default function Expenses() {
  const user = JSON.parse(localStorage.getItem("user") || "{}")
  const [expenses, setExpenses] = useState<any[]>([])
  const [filtered, setFiltered] = useState<any[]>([])
  const [search, setSearch] = useState("")
  const [categoryFilter, setCategoryFilter] = useState("All")
  const [loading, setLoading] = useState(true)

  const fetchExpenses = () => {
    if (!user.id) return
    getExpenses(user.id).then(res => {
      setExpenses(res.data)
      setFiltered(res.data)
    }).catch(console.error).finally(() => setLoading(false))
  }

  useEffect(() => { fetchExpenses() }, [])

  useEffect(() => {
    let result = expenses
    if (categoryFilter !== "All") result = result.filter(e => e.category === categoryFilter)
    if (search) result = result.filter(e =>
      (e.description || "").toLowerCase().includes(search.toLowerCase()) ||
      e.category.toLowerCase().includes(search.toLowerCase())
    )
    setFiltered(result)
  }, [search, categoryFilter, expenses])

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this expense?")) return
    try {
      await deleteExpense(user.id, id)
      fetchExpenses()
    } catch (err) { console.error(err) }
  }

  const categories = ["All", ...new Set(expenses.map(e => e.category))]
  const total = filtered.reduce((sum: number, e: any) => sum + Number(e.amount), 0)

  if (loading) return <div className="flex items-center justify-center h-64"><p className="text-gray-400">Loading...</p></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Expense List</h1>
          <p className="text-sm text-gray-500 mt-1">{filtered.length} expenses · Total: ₱{total.toLocaleString("en-PH", { minimumFractionDigits: 2 })}</p>
        </div>
        <Link to="/add-expense" className="bg-blue-600 text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700">+ Add Expense</Link>
      </div>

      <div className="flex gap-4 mb-6">
        <input type="text" placeholder="Search by description or category..." value={search} onChange={e => setSearch(e.target.value)}
          className="flex-1 border border-gray-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        <select value={categoryFilter} onChange={e => setCategoryFilter(e.target.value)}
          className="border border-gray-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
          {categories.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        {filtered.length === 0 ? (
          <div className="p-12 text-center text-gray-400">
            <p className="text-lg mb-2">No expenses found</p>
            <Link to="/add-expense" className="text-blue-600 font-medium hover:underline">Add your first expense</Link>
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">#</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Date</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Category</th>
                <th className="text-left px-6 py-3 text-gray-500 font-medium">Description</th>
                <th className="text-right px-6 py-3 text-gray-500 font-medium">Amount (₱)</th>
                <th className="text-center px-6 py-3 text-gray-500 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {filtered.map((exp, idx) => (
                <tr key={exp.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-gray-400">{idx + 1}</td>
                  <td className="px-6 py-4 text-gray-700">{exp.date}</td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 rounded-full text-xs font-medium bg-blue-50 text-blue-700">{exp.category}</span>
                  </td>
                  <td className="px-6 py-4 text-gray-700">{exp.description || "—"}</td>
                  <td className="px-6 py-4 text-right font-semibold text-gray-800">₱{Number(exp.amount).toLocaleString("en-PH", { minimumFractionDigits: 2 })}</td>
                  <td className="px-6 py-4 text-center">
                    <button onClick={() => handleDelete(exp.id)}
                      className="text-red-500 hover:text-red-700 text-xs font-medium hover:underline">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
