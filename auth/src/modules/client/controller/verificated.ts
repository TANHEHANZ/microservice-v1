// import { API } from "@shared/index";
// import { Request, Response } from "express";
// import { decryptToken } from "@/infraestructure/helpers/jwt.decript";

// export const verificacionJWT = async (
//   req: Request,
//   res: Response
// ): Promise<void> => {
//   try {
//     const { token } = req.body;

//     if (!token) {
//       API.badRequest(res, "Token is required");
//       return;
//     }

//     const payload = (await decryptToken(token)) as any;

//     API.success(res, "Token verified successfully", {
//       client_id: payload.client_id,
//       scopes: payload.scopes,
//       type: payload.type,
//       exp: payload.exp,
//     });
//   } catch (error) {
//     console.error("Token verification failed:", error);
//     API.unauthorized(res, "Invalid token");
//   }
// };
