import { BrowserRouter, Routes, Route } from "react-router-dom"
import Login from "./features/auth/Login"
import Register from "./features/auth/Register"
import Dashboard from "./features/expense/Dashboard"
import Expenses from "./features/expense/Expenses"
import AddExpense from "./features/expense/AddExpense"
import OAuthCallback from "./features/auth/OAuthCallback"
import Layout from "./shared/Layout"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/oauth-callback" element={<OAuthCallback />} />
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/expenses" element={<Expenses />} />
          <Route path="/add-expense" element={<AddExpense />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
