// import { Request, Response, NextFunction } from "express";
// import { decryptPayload } from "@/infraestructure/middleware/crypto";
// import { API } from "../config/response";

// export const validAcces = async (
//   req: Request,
//   res: Response,
//   next: NextFunction
// ): Promise<void> => {
//   try {
//     const token = req.cookies.accessToken;

//     if (!token) {
//       API.unauthorized(res, "Token no proporcionado");
//       return;
//     }
//     console.log(token);

//     const [iv, ciphertext, authTag] = token.split(".");

//     if (!iv || !ciphertext || !authTag) {
//       API.unauthorized(res, "Token manipulado");
//       return;
//     }

//     const payload = decryptPayload(`${ciphertext}.${authTag}`, iv);

//     if (!payload.exp || Date.now() > payload.exp) {
//       API.unauthorized(res, "Token expirado");
//       return;
//     }
//     (req as any).user = payload;
//     console.log("informacion decodificado", payload);
//     next();
//   } catch (error) {
//     API.unauthorized(res, "Token inválido o corrupto");
//     return;
//   }
// };
