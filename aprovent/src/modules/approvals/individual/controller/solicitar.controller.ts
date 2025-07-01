import { Request, Response } from "express";

import { API } from "@/infraestructure/config/response";
import { sendApprovels } from "../service/send.service";

export const solicitarController = {
  solicitud: async (req: Request, res: Response) => {
    try {
      const result = await sendApprovels({
        documento: req.file,
        ...req.body,
      });
      API.success(res, "Notificación enviada correctamente", result);
    } catch (error: any) {
      console.error(" Error al enviar notificación:", error);
      if (error.status && error.mensaje) {
        API.custom(res, error.mensaje, error.detalle, error.status);
      } else {
        API.serverError(res, "Error inesperado", error);
      }
    }
  },
};
