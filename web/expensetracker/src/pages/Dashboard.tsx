import { Link } from "react-router-dom"

export default function Dashboard() {

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">

      <div className="bg-white p-10 rounded-xl shadow-md text-center w-[400px]">

        <h1 className="text-3xl font-bold mb-4">
          Welcome 👋
        </h1>

        <p className="text-gray-500 mb-6">
          You are now logged into your Expense Tracker dashboard.
        </p>

        <Link
          to="/"
          className="inline-block bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700"
        >
          Logout
        </Link>

      </div>

    </div>
  )
}