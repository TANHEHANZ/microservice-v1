export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: unknown;
}

export interface RequestConfig {
  headers?: Record<string, string>;
  timeout?: number;
}
