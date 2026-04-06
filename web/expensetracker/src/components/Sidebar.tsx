import { Link, useLocation, useNavigate } from "react-router-dom"

const menuItems = [
  { path: "/dashboard", label: "Dashboard", icon: "⊞" },
  { path: "/expenses", label: "Expenses", icon: "☰" },
  { path: "/add-expense", label: "Add Expense", icon: "+" },
]

export default function Sidebar() {
  const location = useLocation()
  const navigate = useNavigate()
  const user = JSON.parse(localStorage.getItem("user") || "{}")

  const handleLogout = () => {
    localStorage.removeItem("user")
    navigate("/")
  }

  return (
    <aside className="w-64 bg-white border-r border-gray-200 min-h-screen flex flex-col">
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">$</div>
          <span className="font-bold text-lg text-gray-800">Expense Tracker</span>
        </div>
      </div>

      <div className="p-4 border-b border-gray-100">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-semibold text-sm">
            {(user.firstname || "U")[0].toUpperCase()}
          </div>
          <div>
            <p className="text-sm font-semibold text-gray-800">{user.firstname} {user.lastname}</p>
            <p className="text-xs text-gray-400">{user.email}</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 p-4 space-y-1">
        {menuItems.map((item) => (
          <Link key={item.path} to={item.path}
            className={`flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              location.pathname === item.path
                ? "bg-blue-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`}>
            <span className="w-5 text-center text-base font-bold">{item.icon}</span>
            {item.label}
          </Link>
        ))}
      </nav>

      <div className="p-4 border-t border-gray-200">
        <button onClick={handleLogout}
          className="w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium text-red-500 hover:bg-red-50 transition-colors">
          <span className="w-5 text-center text-base">←</span>
          Logout
        </button>
      </div>
    </aside>
  )
}