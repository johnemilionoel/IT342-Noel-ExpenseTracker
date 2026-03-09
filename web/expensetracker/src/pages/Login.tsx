import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import axios from "axios"

export default function Login() {

  const navigate = useNavigate()

  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()

    try {

      const response = await axios.post(
        "http://localhost:8081/api/auth/login",
        {
          email: email,
          password: password
        }
      )

      console.log(response.data)

      // redirect to dashboard after successful login
      navigate("/dashboard")

    } catch (error) {

      alert("Invalid email or password")

    }
  }

  const googleLogin = () => {
    window.location.href = "http://localhost:8081/oauth2/authorization/google"
  }

  return (

    <div className="flex items-center justify-center h-screen bg-gray-100">

      <div className="w-[380px] bg-white p-8 rounded-xl shadow-md">

        <h2 className="text-2xl font-bold mb-2">
          Welcome back
        </h2>

        <p className="text-gray-500 mb-6">
          Enter your credentials to access your account
        </p>

        <form onSubmit={handleLogin}>

          <input
            type="email"
            placeholder="Email Address"
            className="w-full border rounded-lg p-3 mb-4"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <input
            type="password"
            placeholder="Password"
            className="w-full border rounded-lg p-3 mb-4"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700"
          >
            Sign In
          </button>

        </form>

        <div className="flex items-center my-6">
          <div className="flex-grow border"></div>
          <span className="mx-3 text-gray-400 text-sm">
            OR CONTINUE WITH
          </span>
          <div className="flex-grow border"></div>
        </div>

        <button
          onClick={googleLogin}
          className="w-full border py-3 rounded-lg hover:bg-gray-50"
        >
          Continue with Google
        </button>

        <p className="text-sm text-center mt-6">

          Don't have an account?{" "}

          <Link
            to="/register"
            className="text-blue-600 font-medium"
          >
            Create one now
          </Link>

        </p>

      </div>

    </div>

  )
}