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
    url: "https://elhacker.info/manuales/Lenguajes%20de%20Programacion/Codigo%20limpio%20-%20Robert%20Cecil%20Martin.pdf",
    etiqueta: "Documento de prueba",
    hash: "",
    tipo: "APROBACION",
  },
];
export const FormNotificacion: DTO_nFormulario = {
  url: "https://elhacker.info/manuales/Lenguajes%20de%20Programacion/Codigo%20limpio%20-%20Robert%20Cecil%20Martin.pdf",
  etiqueta: "Documento de prueba",
  hash: "23ec584b7df618b7c50fff5bca1f5f2a084cfcec881087ce651bc57a87632cbe",
  tipo: "APROBACION",
};

export const notificador: string = JSON.stringify(dataNotifcador);
export const autoridad: string = JSON.stringify(dataAutoridad);

export const getNotificadosRaw = (): string[] => serializarArray(Notificados);
