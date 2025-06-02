import { z } from "zod";

export const createGroup = z.object({
  name: z.string(),
  idService: z.array(z.string()),
  status: z.string(),
  id_group: z.string(),
});
export type createGroupDTO = z.infer<typeof createGroup>;
