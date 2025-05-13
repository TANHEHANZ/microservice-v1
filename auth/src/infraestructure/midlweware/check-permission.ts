import { Response, NextFunction, RequestHandler } from "express";
import { API } from "@firma-gamc/shared";
import { prisma } from "../config/prisma.client";
export const checkPermission = (requiredPermission: string): RequestHandler => {
  return async (req: any, res: Response, next: NextFunction): Promise<void> => {
    try {
      const permission = await prisma.permissons.findFirst({
        where: {
          name: requiredPermission,
          isActive: true,
        },
      });

      if (!permission && requiredPermission !== "*") {
        API.forbidden(res, "Permission not found or inactive");
        return;
      }
      //  debemos validar tanto de los que viene por clientes como de los usuarios

      if (req.decodeAuth.scopes) {
        const hasScope = req.decodeAuth.scopes.some((scope: any) => {
          console.log("viene del token:", scope);
          console.log("Permiso requerido:", requiredPermission);
          return scope === requiredPermission || scope === "*";
        });
        if (hasScope) {
          next();
          return;
        }
      }

      next();
    } catch (error) {
      API.serverError(res, undefined, error);
    }
  };
};
