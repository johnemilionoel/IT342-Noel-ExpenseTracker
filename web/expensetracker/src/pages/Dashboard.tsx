import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { getExpenseSummary, getExpenses } from "../services/api"
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell
} from "recharts"

// Shared color map: badge classes + hex for the donut
const CATEGORY_STYLE: Record<string, { badge: string; hex: string }> = {
  "Food & Dining":    { badge: "bg-blue-100 text-blue-700",   hex: "#4F86E7" },
  "Transportation":   { badge: "bg-green-100 text-green-700", hex: "#34D399" },
  "Office Supplies":  { badge: "bg-purple-100 text-purple-700", hex: "#A78BFA" },
  "Utilities":        { badge: "bg-yellow-100 text-yellow-700", hex: "#FBBF24" },
  "Healthcare":       { badge: "bg-red-100 text-red-700",     hex: "#F87171" },
  "Entertainment":    { badge: "bg-pink-100 text-pink-700",   hex: "#F472B6" },
  "Other":            { badge: "bg-gray-100 text-gray-700",   hex: "#9CA3AF" },
}
const DEFAULT_STYLE = { badge: "bg-gray-100 text-gray-700", hex: "#9CA3AF" }

const getStyle = (name: string) => CATEGORY_STYLE[name] || DEFAULT_STYLE

export default function Dashboard() {
  const user = JSON.parse(localStorage.getItem("user") || "{}")
  const [summary, setSummary] = useState<any>({ totalExpenses: 0, totalTransactions: 0, byCategory: [] })
  const [recentExpenses, setRecentExpenses] = useState<any[]>([])
  const [monthlyData, setMonthlyData] = useState<any[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user.id) return
    Promise.all([
      getExpenseSummary(user.id),
      getExpenses(user.id),
    ]).then(([summaryRes, expensesRes]) => {
      setSummary(summaryRes.data)
      setRecentExpenses(expensesRes.data.slice(0, 5))
      buildMonthlyTrend(expensesRes.data)
    }).catch(console.error).finally(() => setLoading(false))
  }, [])

  const buildMonthlyTrend = (expenses: any[]) => {
    const months: Record<string, number> = {}
    const now = new Date()
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      months[d.toISOString().slice(0, 7)] = 0
    }
    for (const exp of expenses) {
      const key = exp.date.slice(0, 7)
      if (key in months) months[key] += Number(exp.amount)
    }
    const mn = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
    setMonthlyData(Object.entries(months).map(([k, t]) => ({
      month: mn[parseInt(k.split("-")[1]) - 1],
      total: Math.round(t * 100) / 100,
    })))
  }

  const formatYAxis = (v: number) => v >= 1000 ? `₱${(v/1000).toFixed(0)}k` : `₱${v}`
  const totalExp = Number(summary.totalExpenses || 0)
  const pieData = (summary.byCategory || []).map((cat: any) => ({
    name: cat.category,
    value: Number(cat.total),
    pct: totalExp > 0 ? Math.round((Number(cat.total) / totalExp) * 100) : 0,
    hex: getStyle(cat.category).hex,
  }))

  if (loading) return <div className="flex items-center justify-center h-64"><p className="text-gray-400">Loading...</p></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Good morning, {user.firstname} 👋</h1>
          <p className="text-gray-500 text-sm mt-1">Here's an overview of your expense activity.</p>
        </div>
        <Link to="/add-expense" className="bg-blue-600 text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
          + Add Expense
        </Link>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm">
          <p className="text-sm text-gray-500 mb-1">Total Expenses</p>
          <p className="text-3xl font-bold text-gray-800">₱{totalExp.toLocaleString("en-PH", { minimumFractionDigits: 2 })}</p>
        </div>
        <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm">
          <p className="text-sm text-gray-500 mb-1">Total Transactions</p>
          <p className="text-3xl font-bold text-gray-800">{summary.totalTransactions}</p>
        </div>
        <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm">
          <p className="text-sm text-gray-500 mb-1">Categories Used</p>
          <p className="text-3xl font-bold text-gray-800">{summary.byCategory?.length || 0}</p>
        </div>
      </div>

      {/* Spending Trend + Donut */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        <div className="lg:col-span-2 bg-white rounded-xl border border-gray-100 shadow-sm p-6">
          <div className="mb-4">
            <h2 className="font-semibold text-gray-800">Spending Trend</h2>
            <p className="text-sm text-gray-400">Last 6 months</p>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <LineChart data={monthlyData} margin={{ top: 5, right: 20, bottom: 5, left: 10 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" vertical={false} />
              <XAxis dataKey="month" axisLine={false} tickLine={false} tick={{ fill: "#9ca3af", fontSize: 13 }} dy={10} />
              <YAxis axisLine={false} tickLine={false} tick={{ fill: "#9ca3af", fontSize: 12 }} tickFormatter={formatYAxis} width={50} />
              <Tooltip
                formatter={(value: number) => [`₱${value.toLocaleString("en-PH", { minimumFractionDigits: 2 })}`, "Total"]}
                contentStyle={{ borderRadius: "10px", border: "1px solid #e5e7eb", boxShadow: "0 4px 12px rgba(0,0,0,0.08)", fontSize: "13px" }}
              />
              <Line type="monotone" dataKey="total" stroke="#4F86E7" strokeWidth={2.5}
                dot={{ r: 5, fill: "#4F86E7", stroke: "#fff", strokeWidth: 2 }} activeDot={{ r: 7 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Donut */}
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
          <div className="mb-2">
            <h2 className="font-semibold text-gray-800">By Category</h2>
            <p className="text-sm text-gray-400">{new Date().toLocaleString("en-US", { month: "long", year: "numeric" })}</p>
          </div>
          {pieData.length === 0 ? (
            <div className="flex items-center justify-center h-48 text-gray-400 text-sm">No data yet</div>
          ) : (
            <>
              <div className="flex justify-center my-2">
                <ResponsiveContainer width={200} height={200}>
                  <PieChart>
                    <Pie data={pieData} cx="50%" cy="50%" innerRadius={55} outerRadius={85}
                      paddingAngle={3} dataKey="value" stroke="none">
                      {pieData.map((entry: any, i: number) => (
                        <Cell key={i} fill={entry.hex} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(value: number, name: string) => [`₱${value.toLocaleString("en-PH", { minimumFractionDigits: 2 })}`, name]}
                      contentStyle={{ borderRadius: "10px", border: "1px solid #e5e7eb", fontSize: "13px" }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="space-y-3 mt-2">
                {pieData.map((cat: any, i: number) => (
                  <div key={i} className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="w-3 h-3 rounded-full inline-block" style={{ backgroundColor: cat.hex }}></span>
                      <span className="text-sm text-gray-700">{cat.name}</span>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-sm text-gray-500">₱{cat.value.toLocaleString("en-PH", { minimumFractionDigits: 2 })}</span>
                      <span className="text-sm font-semibold text-gray-700 w-10 text-right">{cat.pct}%</span>
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>
      </div>

      {/* Recent Expenses */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between p-6 border-b border-gray-100">
          <h2 className="font-semibold text-gray-800">Recent Expenses</h2>
          <Link to="/expenses" className="text-blue-600 text-sm font-medium hover:underline">View All →</Link>
        </div>
        {recentExpenses.length === 0 ? (
          <div className="p-12 text-center text-gray-400">
            <p className="text-lg mb-2">No expenses yet</p>
            <Link to="/add-expense" className="text-blue-600 font-medium hover:underline">Add your first expense</Link>
          </div>
        ) : (
          <div className="divide-y divide-gray-50">
            {recentExpenses.map((exp: any) => (
              <div key={exp.id} className="flex items-center justify-between px-6 py-4">
                <div className="flex items-center gap-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStyle(exp.category).badge}`}>
                    {exp.category}
                  </span>
                  <div>
                    <p className="text-sm font-medium text-gray-800">{exp.description || "—"}</p>
                    <p className="text-xs text-gray-400">{exp.date}</p>
                  </div>
                </div>
                <p className="font-semibold text-gray-800">₱{Number(exp.amount).toLocaleString("en-PH", { minimumFractionDigits: 2 })}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}