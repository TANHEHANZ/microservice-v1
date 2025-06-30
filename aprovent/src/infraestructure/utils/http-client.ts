import axios, { AxiosRequestConfig } from "axios";
import config from "../config/config";

const http = axios.create({
  baseURL: config.API.notifications,
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${config.token.notifications}`,
  },
  timeout: 10000,
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.mensaje || error.message;

    console.error(`🛑 Error ${status}: ${message}`);
    return Promise.reject({
      status,
      message,
      data: error.response?.data || null,
    });
  }
);

export default http;
