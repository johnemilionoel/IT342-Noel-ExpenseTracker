import axios from "axios";

const API_BASE = "http://localhost:8080/api";

const api = axios.create({ baseURL: API_BASE });

// Auth
export const registerUser = (data: any) => api.post("/auth/register", data);
export const loginUser = (data: any) => api.post("/auth/login", data);

// Expenses
export const getExpenses = (userId: number) => api.get(`/expenses?userId=${userId}`);
export const addExpense = (userId: number, data: any) => api.post(`/expenses?userId=${userId}`, data);
export const updateExpense = (userId: number, id: number, data: any) => api.put(`/expenses/${id}?userId=${userId}`, data);
export const deleteExpense = (userId: number, id: number) => api.delete(`/expenses/${id}?userId=${userId}`);
export const getExpenseSummary = (userId: number) => api.get(`/expenses/summary?userId=${userId}`);

// Categories
export const getCategories = () => api.get("/categories");

export default api;
