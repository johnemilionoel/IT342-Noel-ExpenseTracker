import { useState, useEffect } from "react"
import { Link, useNavigate, useSearchParams } from "react-router-dom"
import { loginUser, getUser } from "../services/api"

export default function Login() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (params.get("error") === "oauth_failed") {
      setError("Google sign-in failed. Please try again.")
    }
  }, [params])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!email || !password) { setError("Please fill in all fields"); return }

    setLoading(true)
    try {
      const response = await loginUser({ email, password })
      const msg = response.data

      // Handle both plain string and ApiResponse wrapper
      const isSuccess = typeof msg === "string"
        ? msg.includes("Login successful")
        : (msg.success === true || (msg.message && msg.message.includes("Login successful")))

      if (isSuccess) {
        const userRes = await getUser(email)
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
          <span className="mx-3 text-gray-400 text-xs uppercase tracking-wide">or continue with</span>
          <div className="flex-grow border-t border-gray-200"></div>
        </div>

        <button onClick={googleLogin}
          className="w-full flex items-center justify-center gap-3 border border-gray-200 py-3 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-all">
          <svg width="18" height="18" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
            <path d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844a4.14 4.14 0 0 1-1.796 2.716v2.259h2.908c1.702-1.567 2.684-3.875 2.684-6.615Z" fill="#4285F4"/>
            <path d="M9 18c2.43 0 4.467-.806 5.956-2.18l-2.908-2.259c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332A8.997 8.997 0 0 0 9 18Z" fill="#34A853"/>
            <path d="M3.964 10.71A5.41 5.41 0 0 1 3.682 9c0-.593.102-1.17.282-1.71V4.958H.957A8.997 8.997 0 0 0 0 9c0 1.452.348 2.827.957 4.042l3.007-2.332Z" fill="#FBBC05"/>
            <path d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0A8.997 8.997 0 0 0 .957 4.958L3.964 7.29C4.672 5.163 6.656 3.58 9 3.58Z" fill="#EA4335"/>
          </svg>
          Sign in with Google
        </button>

        <p className="text-sm text-center mt-6 text-gray-500">
          Don't have an account?{" "}
          <Link to="/register" className="text-blue-600 font-medium hover:underline">Create one now</Link>
        </p>
      </div>
    </div>
  )
}