import { z } from "zod";
import { StatusEnum } from "@prisma/client";

const BaseServiceSchema = z.object({
  name: z.string(),
  description: z.string(),
  icon: z.string(),
  s_tipoId: z.string(),
  s_configurationId: z.string(),
  s_grupoId: z.string(),
  status: z.nativeEnum(StatusEnum),
  createdAt: z.string(),
  updatedAt: z.string(),
});

const CreateServiceSchema = z.object({
  name: z.string(),
  description: z.string(),
  icon: z.string(),
  s_tipoId: z.string(),
  s_configurationId: z.array(z.string()),
  s_grupoId: z.string(),
});

export type createServiceDTO = z.infer<typeof CreateServiceSchema>;
export type baseServiceDTO = z.infer<typeof BaseServiceSchema>;

export { CreateServiceSchema, BaseServiceSchema };
