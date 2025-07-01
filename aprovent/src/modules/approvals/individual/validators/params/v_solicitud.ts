import { z } from "zod";

export const SchemaSolicitud = z.object({
  tipoDocumento: z.enum(["PDF"]),
  descripcion: z.string(),
  accessToken: z.string(),
  documento: z.string(),
});

export type DTO_apSolicitud = z.infer<typeof SchemaSolicitud>;
