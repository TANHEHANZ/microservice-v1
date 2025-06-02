import { Request, Response } from "express";
import { groupService } from "../services/group.service";
import { controllerHandler } from "@/infraestructure/helpers/controllerHandler";

export const GroupController = {
  AllGroups: async (req: Request, res: Response): Promise<void> => {
    await controllerHandler(res, groupService.all, "Todos los grupos");
  },

  createGroup: async (req: Request, res: Response): Promise<void> => {
    await controllerHandler(
      res,
      () => groupService.create(req.body),
      "Grupo creado exitosamente"
    );
  },
  groupServices: async (req: Request, res: Response): Promise<void> => {
    await controllerHandler(
      res,
      () => groupService.groupServices(req.body),
      "Servicios agregados al grupo"
    );
  },

  putServices: async (req: Request, res: Response): Promise<void> => {
    // Implementación futura
  },

  deletedServices: async (req: Request, res: Response): Promise<void> => {
    // Implementación futura
  },
};
