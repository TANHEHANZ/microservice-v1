import { Response } from "express";
import { API } from "@/infraestructure/config/response";

export async function controllerHandler<T>(
  res: Response,
  serviceCall: () => Promise<T>,
  successMessage = "Operación exitosa"
): Promise<void> {
  try {
    const result: any = await serviceCall();

    if (!result.success) {
      API.badRequest(res, result.message, result.error);
      return;
    }

    API.success(res, successMessage, result.data || result);
  } catch (error) {
    API.serverError(res, "Error crítico. Contacta con soporte", error);
  }
}
