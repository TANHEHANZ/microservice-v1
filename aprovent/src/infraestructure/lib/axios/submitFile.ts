import config from "@/infraestructure/config/config";
import { createApiInstance } from "./config.axios";

export interface FileResponse {
  id_repository: string;
  file_name: string;
  url_file: string;
  size: number;
  type: string;
  collector: string;
  nro_documento: string;
  created_at: string;
  status: number;
}

export interface RepositoryResponse {
  status: boolean;
  response: FileResponse[];
}
export const submitFile = createApiInstance({
  baseURL: config.API.upload,
  timeout: 15000,
});
