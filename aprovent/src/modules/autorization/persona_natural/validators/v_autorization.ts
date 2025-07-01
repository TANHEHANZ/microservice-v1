import { z } from "zod";

const AutorizacionSchema = z.object({
  titulo: z.string(),
  descripcion: z.string(),
  notificador: z.string(),
  notificado: z.string(),
});

const SeguridadSchema = z.object({
  llaveSimetrica: z.string(),
  iv: z.string(),
});

const Schema = z.object({
  autorizacion: AutorizacionSchema,
  seguridad: SeguridadSchema,
  sha256: z.string(),
});
export type DTO_MainAutorization = z.infer<typeof Schema>;
