import { useEffect } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"

export default function OAuthCallback() {
  const navigate = useNavigate()
  const [params] = useSearchParams()

  useEffect(() => {
    const id = params.get("id")
    const email = params.get("email")
    const firstname = params.get("firstname")
    const lastname = params.get("lastname")

    if (id && email) {
      // Store user data from OAuth callback
      const userData = {
        id: Number(id),
        email: decodeURIComponent(email),
        firstname: decodeURIComponent(firstname || ""),
        lastname: decodeURIComponent(lastname || ""),
      }
      localStorage.setItem("user", JSON.stringify(userData))
      navigate("/dashboard")
    } else {
      // OAuth failed — redirect to login
      navigate("/?error=oauth_failed")
    }
  }, [params, navigate])

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <p className="text-gray-600 text-sm">Signing you in with Google...</p>
      </div>
    </div>
  )
}
