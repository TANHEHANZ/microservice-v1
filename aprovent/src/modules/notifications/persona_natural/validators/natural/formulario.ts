import { z } from "zod";

const FormularioSchema = z.object({
  etiqueta: z.string(),
  url: z.string().url(),
  tipo: z.enum(["FIRMA", "APROBACION"]),
  hash: z.string(),
});

export type DTO_nFormulario = z.infer<typeof FormularioSchema>;
