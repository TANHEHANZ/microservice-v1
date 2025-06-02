declare global {
  type ResponseS = {
    success: boolean;
    message: string;
    data?: any;
    error?: string;
  };
}

export {};
