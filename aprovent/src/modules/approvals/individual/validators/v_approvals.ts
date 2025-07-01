import { z } from "zod";

const SchemaApprovals = z.object({
  tipoDocumento: z.string(),
  hashDocumento: z.string(),
  descripcion: z.string(),
  idTramite: z.string().uuid("Debe ser un UUID válido"),
  accessToken: z.string(),
  documento: z.string(),
});

export type DTO_MainApprovals = z.infer<typeof SchemaApprovals>;
