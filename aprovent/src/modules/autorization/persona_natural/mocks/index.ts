import { DTO_apNotificado } from "../validators/v_apNotificado";
import { DTO_apNotificador } from "../validators/v_apNotificador";

const dataNotifcador: DTO_apNotificador = {
  numeroDocumento: "7589024023-1Y",
  tipoDocumento: "CI",
  fechaNacimiento: "1994-07-06",
};
const dataNotificado: DTO_apNotificado = {
  tipoDocumento: "CI",
  numeroDocumento: "7213213346-7W",
  fechaNacimiento: "1962-08-07",
};

export const A_notificador: string = JSON.stringify(dataNotifcador);
export const A_notificado: string = JSON.stringify(dataNotificado);
