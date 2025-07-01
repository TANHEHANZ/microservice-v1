import { z } from "zod";

const notificadorSchema = z.object({
  tipoDocumento: z.string().max(5),
  numeroDocumento: z.string().max(20),
  fechaNacimiento: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, {
    message: "Debe tener formato YYYY-MM-DD",
  }),
});

export type DTO_apNotificador = z.infer<typeof notificadorSchema>;
