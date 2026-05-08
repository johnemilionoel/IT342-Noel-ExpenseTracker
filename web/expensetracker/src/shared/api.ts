import axios from "axios";

const API_BASE = "http://localhost:8080/api";
const api = axios.create({ baseURL: API_BASE });

// Unwrap ApiResponse envelope: { success, data, message } → returns just the .data field
const unwrap = (res: any) => {
  if (res.data && typeof res.data === "object" && "success" in res.data && "data" in res.data) {
    return { ...res, data: res.data.data };
  }
  return res;
};

export const registerUser = (data: any) => api.post("/auth/register", data);
export const loginUser = (data: any) => api.post("/auth/login", data);
export const getUser = (email: string) => api.get(`/auth/user?email=${email}`).then(unwrap);

// Expenses (unwrap ApiResponse)
export const getExpenses = (userId: number) => api.get(`/expenses?userId=${userId}`).then(unwrap);
export const addExpense = (userId: number, data: any) => api.post(`/expenses?userId=${userId}`, data).then(unwrap);
export const updateExpense = (userId: number, id: number, data: any) => api.put(`/expenses/${id}?userId=${userId}`, data).then(unwrap);
export const deleteExpense = (userId: number, id: number) => api.delete(`/expenses/${id}?userId=${userId}`).then(unwrap);
export const getExpenseSummary = (userId: number) => api.get(`/expenses/summary?userId=${userId}`).then(unwrap);

// Categories
export const getCategories = () => api.get("/categories");

export default api;