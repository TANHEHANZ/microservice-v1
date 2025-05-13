import { NextFunction, Request, Response } from "express";
import { API } from "@firma-gamc/shared";
import { decryptToken } from "../helpers/jwt.decript";

export const authMiddleware = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      API.unauthorized(res, "No token provided");
      return;
    }
    const token = authHeader.split(" ")[1];
    const decodedToken = (await decryptToken(token)) as any;

    if (!decodedToken) {
      API.unauthorized(res, "Token no proporcionado");
    }
    console.log("token decodificado ", decodedToken);
    const decode = {
      client_id: decodedToken.client_id,
      scopes: decodedToken.scopes,
      type: decodedToken.type,
      exp: decodedToken.exp,
    };
    req.decodeAuth = decode;
    console.log(decode);
    next();
  } catch (error: any) {
    console.error("Error en el middleware de autenticación:", error);
    if (error.name === "TokenExpiredError") {
      API.unauthorized(res, "Token expirado");
    } else {
      API.unauthorized(res, "Token inválido");
    }
  }
};
