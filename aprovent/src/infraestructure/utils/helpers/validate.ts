import { API } from "@/infraestructure/config/response";
import { Request, Response, NextFunction } from "express";
import { ZodError, ZodSchema } from "zod";
import { fromError } from "zod-validation-error";
export const validate =
  (schema: ZodSchema<any>, type: "body" | "query" | "file" = "body") =>
  (req: Request, res: Response, next: NextFunction): void => {
    try {
      let dataToValidate;

      switch (type) {
        case "query":
          dataToValidate = req.query;
          break;
        case "file":
          if (!req.file) {
            throw new Error("No se encontró el archivo");
          }
          dataToValidate = req.file;
          break;
        default:
          dataToValidate = req.body;
      }

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
