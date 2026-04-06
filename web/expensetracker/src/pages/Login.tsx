import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { loginUser } from "../services/api"
import axios from "axios"

export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!email || !password) { setError("Please fill in all fields"); return }

    setLoading(true)
    try {
      const response = await loginUser({ email, password })
      const msg = response.data

      if (typeof msg === "string" && msg.includes("Login successful")) {
        // Fetch user info to get the id
        const userRes = await axios.get(`http://localhost:8080/api/auth/user?email=${email}`)
        const userData = userRes.data
        localStorage.setItem("user", JSON.stringify(userData))
        navigate("/dashboard")
      } else {
        setError("Invalid email or password")
      }
    } catch (err) {
      setError("Invalid email or password")
    } finally { setLoading(false) }
  }

  const googleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google"
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="w-[420px] bg-white p-8 rounded-2xl shadow-lg border border-gray-100">
        <div className="text-center mb-8">
          <div className="w-14 h-14 bg-blue-600 rounded-xl flex items-center justify-center text-white font-bold text-2xl mx-auto mb-4">$</div>
          <h1 className="text-2xl font-bold text-gray-800">Welcome back</h1>
          <p className="text-gray-500 text-sm mt-1">Enter your credentials to access your account</p>
        </div>

        {error && <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg text-sm mb-4">{error}</div>}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
            <input type="email" placeholder="your@email.com" value={email} onChange={e => setEmail(e.target.value)}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Password</label>
            <input type="password" placeholder="••••••••" value={password} onChange={e => setPassword(e.target.value)}
              className="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <button type="submit" disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-50">
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <div className="flex items-center my-6">
          <div className="flex-grow border-t border-gray-200"></div>
          <span className="mx-3 text-gray-400 text-xs">OR</span>
          <div className="flex-grow border-t border-gray-200"></div>
        </div>

        <button onClick={googleLogin}
          className="w-full border border-gray-200 py-3 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors">
          G &nbsp; Sign in with Google
        </button>

        <p className="text-sm text-center mt-6 text-gray-500">
          Don't have an account?{" "}
          <Link to="/register" className="text-blue-600 font-medium hover:underline">Create one now</Link>
        </p>
      </div>
    </div>
  )
}
