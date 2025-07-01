import FormData from "form-data";
import {
  submitFile,
  RepositoryResponse,
} from "@/infraestructure/lib/axios/submitFile";

export interface PropsUpload {
  key: string;
  value: string | Express.Multer.File;
  type: "file" | "text";
}

export const UploadFile = async (
  props: PropsUpload[]
): Promise<RepositoryResponse> => {
  const formData = new FormData();

  for (const item of props) {
    if (item.type === "file" && typeof item.value !== "string") {
      const file = item.value as Express.Multer.File;
      formData.append(item.key, file.buffer, {
        filename: file.originalname,
        contentType: file.mimetype,
      });
    } else {
      formData.append(item.key, item.value as string);
    }
  }

  const headers = formData.getHeaders();

  const res = await submitFile.post("/presupuesto", formData, {
    headers,
  });
  console.log(JSON.stringify(res, null, 2));
  return res.data;
};
