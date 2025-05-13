import { ResponseS } from "@/infraestructure/types/responseS.type";

export const ValidateQuery = (query: any, origin: string): ResponseS => {
  return {
    success: true,
    message: "Usuario autenticado con exito",
    data: query,
  };
};
