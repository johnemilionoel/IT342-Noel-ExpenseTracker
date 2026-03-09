import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import axios from "axios"

export default function Register() {

  const navigate = useNavigate()

  const [firstname, setFirstname] = useState("")
  const [lastname, setLastname] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()

    try {

      await axios.post(
        "http://localhost:8081/api/auth/register",
        {
          firstname,
          lastname,
          email,
          password
        }
      )

      alert("Account created successfully")

      navigate("/") // go back to login

    } catch (error) {

      alert("Registration failed")

    }
  }

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">

      <div className="w-[380px] bg-white p-8 rounded-xl shadow-md">

        <h2 className="text-2xl font-bold mb-2">Create your account</h2>

        <p className="text-gray-500 mb-6">
          Fill in your details to get started
        </p>

        <form onSubmit={handleRegister}>

          <input
            placeholder="First Name"
            className="w-full border rounded-lg p-3 mb-3"
            value={firstname}
            onChange={(e) => setFirstname(e.target.value)}
          />

          <input
            placeholder="Last Name"
            className="w-full border rounded-lg p-3 mb-3"
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
          />

          <input
            placeholder="Email"
            className="w-full border rounded-lg p-3 mb-3"
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
            Create Account
          </button>

        </form>

        <p className="text-sm text-center mt-6">
          Already have an account?{" "}
          <Link to="/" className="text-blue-600 font-medium">
            Sign in
          </Link>
        </p>

      </div>

    </div>
  )
}