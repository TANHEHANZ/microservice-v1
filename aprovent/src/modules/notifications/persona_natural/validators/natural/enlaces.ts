import { z } from "zod";

const EnlaceSchema = z.object({
  etiqueta: z.string(),
  url: z.string().url(),
  tipo: z.enum(["FIRMA", "APROBACION"]),
  hash: z.string(),
});

const SchemaEnlaces = z.array(EnlaceSchema);

export type DTO_nEnlaces = z.infer<typeof SchemaEnlaces>;
