import { Request, Response } from "express";
import { enviarNotificacion } from "../services/notificar.autoridad";
import { API } from "@/infraestructure/config/response";

export const enviarNotificacionController = async (
  req: Request,
  res: Response
) => {
  try {
    const result = await enviarNotificacion();
    API.success(res, "Notificación enviada correctamente", result);
  } catch (error: any) {
    console.error("❌ Error al enviar notificación:", error);
    if (error.status && error.mensaje) {
      API.custom(res, error.mensaje, error.detalle, error.status);
    } else {
      API.serverError(res, "Error inesperado", error);
    }
  }
};
