import axios from "axios";

const API = "http://localhost:8081/api/auth";

export const registerUser = (data:any) => {
  return axios.post(`${API}/register`, data);
};

export const loginUser = (data:any) => {
  return axios.post(`${API}/login`, data);
};