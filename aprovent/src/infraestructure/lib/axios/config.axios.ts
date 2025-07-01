import axios, { AxiosInstance, AxiosError, AxiosResponse } from "axios";

interface ApiError {
  status: number;
  message: string;
  data?: any;
}

interface ConfigAxiosOptions {
  baseURL: string;
  token?: string;
  authType?: "Bearer" | "Basic" | "Token" | "Custom";
  customAuthHeader?: string;
  timeout?: number;
}

export const createApiInstance = (
  options: ConfigAxiosOptions
): AxiosInstance => {
  const api = axios.create({
    baseURL: options.baseURL,
    timeout: options.timeout || 10000,
    headers: {
      "Content-Type": "application/json",
    },
  });

  api.interceptors.request.use((config) => {
    if (options.token) {
      if (options.customAuthHeader) {
        config.headers[options.customAuthHeader] = options.token;
      } else {
        const prefix =
          options.authType === "Bearer"
            ? "Bearer "
            : options.authType === "Basic"
            ? "Basic "
            : options.authType === "Token"
            ? ""
            : "";
        config.headers.Authorization = `${prefix}${options.token}`;
      }
    }
    return config;
  });
  api.interceptors.response.use(
    (response: AxiosResponse) => {
      return response.data;
    },
    (error: AxiosError): Promise<ApiError> => {
      if (error.response) {
        const apiError: ApiError = {
          status: error.response.status,
          message: (error.response.data as any)?.message || "Error de API",
          data: error.response.data,
        };

        console.error(
          `API Error [${error.response.status}]:`,
          apiError.message
        );
        return Promise.reject(apiError);
      } else if (error.request) {
        const apiError: ApiError = {
          status: 503,
          message: "Servicio no disponible - Sin respuesta del servidor",
        };

        console.error("Network Error:", error.request);
        return Promise.reject(apiError);
      } else {
        const apiError: ApiError = {
          status: 500,
          message: error.message || "Error interno de configuración",
        };

        console.error("Configuration Error:", error.message);
        return Promise.reject(apiError);
      }
    }
  );

  return api;
};
