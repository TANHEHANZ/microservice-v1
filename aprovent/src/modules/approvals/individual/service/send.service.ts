import config from "@/infraestructure/config/config";
import { DTO_apSolicitud } from "../validators/params/v_solicitud";
import { PropsUpload, UploadFile } from "./upload";
import { generarSHA, generarSHAFile } from "@/infraestructure/utils/sha256";

export const sendApprovels = async (data: DTO_apSolicitud) => {
  const hash = await generarSHAFile(data.documento);
  console.log("hash del documento:", hash);
  const props: PropsUpload[] = [
    {
      key: "sistema_id",
      value: config.sistem_id,
      type: "text",
    },
    {
      key: "collector",
      value: "presupuesto",
      type: "file",
    },
    {
      key: "file",
      value: data.documento,
      type: "file",
    },
  ];

  try {
    const resultado = await UploadFile(props);
    return {
      message: "Archivo enviado correctamente",
      data: resultado,
    };
  } catch (error) {
    console.error("Error al subir archivo:", error);
    return {
      error: "Ocurrió un error al subir el archivo.",
      detalles: (error as any).message || error,
    };
  }
};
