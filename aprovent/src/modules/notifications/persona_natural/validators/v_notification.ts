import { TypeOf, z } from "zod";

const datosAdicionalesEntidadSchema = z.object({
  clave: z.string().max(30),
  valor: z.string().max(100),
});

const documentoAdjuntoSchema = z.object({
  etiqueta: z.string(),
  url: z.string(),
  tipo: z.enum(["FIRMA", "APROBACION"]),
  hash: z.string(),
});

const seguridadSchema = z.object({
  llaveSimetrica: z.string(),
  iv: z.string(),
});

const notificacionSchema = z.object({
  datosAdicionalesEntidad: z.array(datosAdicionalesEntidadSchema).optional(),
  titulo: z.string().max(255),
  descripcion: z.string(), // Cifrado simétrico
  notificador: z.string(), // Cifrado simétrico
  autoridad: z.string(), // Cifrado simétrico
  notificados: z.array(z.string()), // Cifrado simétrico por cada uno
  enlaces: z.array(documentoAdjuntoSchema),
  formularioNotificacion: documentoAdjuntoSchema,
  entidadNotificadora: z.string().optional(),
});

const SchemaNotificationNatural = z.object({
  notificacion: notificacionSchema,
  seguridad: seguridadSchema,
});

const MainNotification = z.object({
  ...SchemaNotificationNatural.shape,
  sha256: z.string(),
});

export type DTO_MainNotification = z.infer<typeof MainNotification>;
export type DTO_NotificationNatural = z.infer<typeof notificacionSchema>;
