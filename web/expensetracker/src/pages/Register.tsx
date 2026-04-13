import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { registerUser } from "../services/api"

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ firstname: "", lastname: "", email: "", password: "" })
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!form.firstname || !form.lastname || !form.email || !form.password) { setError("Please fill in all fields"); return }

    setLoading(true)
    try {
      const res = await registerUser(form)
      const msg = typeof res.data === "string" ? res.data : (res.data.message || res.data.data || "")

      if (msg.includes("successfully")) {
          alert("Account created successfully!")
          navigate("/")
      } else if (msg.includes("already")) {
          setError("This email is already registered")
      } else {
          setError(msg || "Registration failed")
      }
    } catch (err) { setError("Registration failed") }
    finally { setLoading(false) }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="w-[420px] bg-white p-8 rounded-2xl shadow-lg border border-gray-100">
        <h2 className="text-2xl font-bold mb-2 text-gray-800">Create your account</h2>
        <p className="text-gray-500 text-sm mb-6">Fill in your details to get started</p>

        {error && <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg text-sm mb-4">{error}</div>}

        <form onSubmit={handleRegister} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">First Name</label>
              <input placeholder="John" value={form.firstname} onChange={e => setForm({...form, firstname: e.target.value})}
                className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">Last Name</label>
              <input placeholder="Noel" value={form.lastname} onChange={e => setForm({...form, lastname: e.target.value})}
                className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
            <input type="email" placeholder="your@email.com" value={form.email} onChange={e => setForm({...form, email: e.target.value})}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Password</label>
            <input type="password" placeholder="••••••••" value={form.password} onChange={e => setForm({...form, password: e.target.value})}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button type="submit" disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-50">
            {loading ? "Creating..." : "Create Account"}
          </button>
        </form>

        <p className="text-sm text-center mt-6 text-gray-500">
          Already have an account?{" "}
          <Link to="/" className="text-blue-600 font-medium hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  )
}
