// import { API } from "@shared/index";
// import { Request, Response } from "express";
// import { prisma } from "@/infraestructure/config/prisma.client";
// import { AuthClientQueryDTO } from "@/infraestructure/models/client/authClient.dto";
// import { validateClientCredentials } from "@/infraestructure/helpers/encript";
// import { generateSecureToken } from "@/infraestructure/helpers/jwt";

// export const authClient = async (
//   req: Request,
//   res: Response
// ): Promise<void> => {
//   try {
//     const { client_id, client_secret } = req.query as AuthClientQueryDTO;
//     const client = await prisma.oAuthClient.findUnique({
//       where: {
//         client_id: client_id,
//       },
//       select: {
//         client_secret: true,
//         client_id: true,
//         oAuthClientScopePermission: {
//           select: {
//             scope: {
//               select: {
//                 name: true,
//               },
//             },
//           },
//         },
//       },
//     });

//     console.log(JSON.stringify(client, null, 2));
//     if (!client) {
//       API.unauthorized(res, "Invalid client credentials");
//       return;
//     }
//     const scopes = [
//       ...new Set(client.oAuthClientScopePermission.map((sp) => sp.scope.name)),
//     ];
//     console.log(scopes);
//     const validatedCredentials = await validateClientCredentials(
//       client_secret,
//       client.client_secret!
//     );
//     if (!validatedCredentials) {
//       API.unauthorized(res, "Invalid client credentials");
//       return;
//     }
//     const access_token = await generateSecureToken({
//       client_id,
//       scopes: scopes,
//       expiresIn: "15m",
//     });
//     const refresh_token = await generateSecureToken({
//       client_id,
//       scopes: scopes,
//       expiresIn: "30d",
//     });

//     API.success(res, "Client authenticated", {
//       access_token: access_token,
//       refresh_token: refresh_token,
//       token_type: "Bearer",
//       expires_in: 60,
//     });
//   } catch (error) {}
// };
