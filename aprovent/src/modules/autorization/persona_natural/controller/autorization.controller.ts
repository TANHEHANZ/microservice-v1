import { Request, Response } from "express";
import { API } from "@/infraestructure/config/response";
import { enviarAuthorization } from "../service/autorizar";

export const enviarNotificacionController = async (
  req: Request,
  res: Response
) => {
  try {
    const result = await enviarAuthorization();
    API.success(res, "Auth enviada correctamente", result);
  } catch (error: any) {
    console.error("❌ Error al enviar notificación:", error);
    if (error.status && error.mensaje) {
      API.custom(res, error.mensaje, error.detalle, error.status);
    } else {
      API.serverError(res, "Error inesperado", error);
    }
  }
};
