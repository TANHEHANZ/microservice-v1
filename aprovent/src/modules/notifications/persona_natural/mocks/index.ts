import { serializarArray } from "@/infraestructure/utils/serializer";
import { DTO_nEnlaces } from "../validators/natural/enlaces";
import { DTO_nNotificador } from "../validators/natural/notificador";
import { DTO_nNotificados } from "../validators/natural/notificados";
import { DTO_nFormulario } from "../validators/natural/formulario";
import { DTO_nAutoridad } from "../validators/natural/autoridad";

const dataNotifcador: DTO_nNotificador = {
  numeroDocumento: "4622712732-3T",
  tipoDocumento: "CI",
  fechaNacimiento: "2005-05-03",
};

const Notificados: DTO_nNotificados = [
  {
    tipoDocumento: "CI",
    numeroDocumento: "7589024023-1Y",
    fechaNacimiento: "1994-07-06",
  },
];
const dataAutoridad: DTO_nAutoridad = {
  tipoDocumento: "CI",
  numeroDocumento: "4622712732-3T",
  fechaNacimiento: "2005-05-03",
};
export const Enlaces: DTO_nEnlaces = [
  {
    url: "https://repositoriogamcdev.cochabamba.bo/repositorio_ddsi/presupuesto/mock_sist_planif_presup_V2_(1)_a85aac0e-f139-4a5d-8a99-e592060349d7.pdf",
    etiqueta:
      "Enlace de prueba con hash del documento antes de subir al repositorio",
    hash: "0920eac8e9951a46416f4f0592346a0641fedd2c6eccce5644223b2af5a79317",
    tipo: "APROBACION",
  },
];
// export const FormNotificacion: DTO_nFormulario = {
//   url: "https://aprobador.ciudadania.demo.agetic.gob.bo/solicitudes/602042df-e315-469d-922d-f90ce3c46362",
//   etiqueta: "Documento subido con aprovaciones con el id del notificador",
//   hash: "23ec584b7df618b7c50fff5bca1f5f2a084cfcec881087ce651bc57a87632cbe",
//   tipo: "APROBACION",
// };

export const FormNotificacion: DTO_nFormulario = {
  url: "https://repositoriogamcdev.cochabamba.bo/repositorio_ddsi/presupuesto/mock_sist_planif_presup_V2_(1)_a85aac0e-f139-4a5d-8a99-e592060349d7.pdf",
  etiqueta: "Enlace de prueba ya fue subido para aprovar",
  hash: "0920eac8e9951a46416f4f0592346a0641fedd2c6eccce5644223b2af5a79317",
  tipo: "APROBACION",
};
export const notificador: string = JSON.stringify(dataNotifcador);
export const autoridad: string = JSON.stringify(dataAutoridad);

export const getNotificadosRaw = (): string[] => serializarArray(Notificados);
