import { API } from "@firma-gamc/shared";
import { Request, Response, NextFunction } from "express";
import { ZodError, ZodSchema } from "zod";
import { fromError } from "zod-validation-error";

export const validate =
  (schema: ZodSchema<any>, type: "body" | "query" = "body") =>
  (req: Request, res: Response, next: NextFunction): void => {
    try {
      const dataToValidate = type === "body" ? req.body : req.query;
      schema.parse(dataToValidate);
      next();
    } catch (error) {
      if (error instanceof ZodError) {
        const e = fromError(error).details;
        API.conflict(res, "Error de validacion", e);
        return;
      }
      API.serverError(
        res,
        "Error de validacion",
        error instanceof Error ? error.message : "Ocurrió un error inesperado"
      );
      return;
    }
  };
