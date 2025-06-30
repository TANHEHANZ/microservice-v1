import { z } from "zod";

const NotificadoSchema = z.object({
  tipoDocumento: z.string().max(5),
  numeroDocumento: z.string().max(20),
  fechaNacimiento: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, {
    message: "Debe tener formato YYYY-MM-DD",
  }),
});

const SchemaNotification = z.array(NotificadoSchema);

export type DTO_nNotificados = z.infer<typeof SchemaNotification>;
