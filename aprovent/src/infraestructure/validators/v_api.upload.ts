import { z } from "zod";

const API_Upload = z.object({
  sistema_id: z.string().uuid(),
  collector: z.string(),
  file: z.any(),
});
export type DTO_APIUpload = z.infer<typeof API_Upload>;
