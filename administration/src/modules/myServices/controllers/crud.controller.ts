import { API } from "@/infraestructure/config/response";
import { Request, Response } from "express";
import { getServiceService } from "../services/getService.service";
import { createServiceService } from "../services/createService";

export const crudServiceController = {
  getAllServices: async (req: Request, res: Response): Promise<void> => {
    try {
      const service = await getServiceService();
      API.success(res, "All services", service);
    } catch (error) {
      API.serverError(res, "Error critico, Contactate con soporte", error);
    }
  },
  createService: async (req: Request, res: Response): Promise<void> => {
    try {
      const create = await createServiceService(req.body);
      if (!create.success) {
        API.badRequest(res, create.message, create.error);
        return;
      }
      API.success(res, create.message, create.data);
    } catch (error) {
      API.serverError(res, "Error critico, Contactate con soporte", error);
    }
  },

  putServices: async (req: Request, res: Response): Promise<void> => {},
  deletedServices: async (req: Request, res: Response): Promise<void> => {},
};
