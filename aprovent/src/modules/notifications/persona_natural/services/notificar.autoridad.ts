import {
  encriptarRSA,
  encriptarSimetricoDatos,
  LlaveSimetricoEntidad,
} from "@/infraestructure/utils/aes-encryption.util";
import { generarClaveYIV } from "@/infraestructure/utils/crypto.util";
import {
  autoridad,
  Enlaces,
  FormNotificacion,
  getNotificadosRaw,
  notificador,
} from "../mocks";
import {
  DTO_MainNotification,
  DTO_NotificationNatural,
} from "../validators/v_notification";
import {
  generarSHA,
  generarSHA256,
  obtenerHashArchivo,
} from "@/infraestructure/utils/sha256";
import config from "@/infraestructure/config/config";

export const enviarNotificacion = async () => {
  const { clave, iv } = generarClaveYIV();

  const llaveSimetricaCifrada = encriptarRSA(clave, config.PEM);
  const ivCifrado = encriptarRSA(iv, config.PEM);
  const claveSimetrica: LlaveSimetricoEntidad = {
    llaveAES: clave,
    ivAES: iv,
  };
  const hash = obtenerHashArchivo("codigoLimpio.pdf");
  console.log("Hash SHA-256:", hash);

  const E_notificados = getNotificadosRaw().map((item) =>
    encriptarSimetricoDatos(item, claveSimetrica)
  );

  const E_enlaces = Enlaces.map((item) => ({
    ...item,
    hash: generarSHA(item.url),
    url: encriptarSimetricoDatos(item.url, claveSimetrica),
  }));

  const E_formularioNotificacion = {
    ...FormNotificacion,
    hash: generarSHA(FormNotificacion.url),
    url: encriptarSimetricoDatos(FormNotificacion.url, claveSimetrica),
  };
  const E_autoridad = encriptarSimetricoDatos(autoridad, claveSimetrica);
  const E_notificador = encriptarSimetricoDatos(notificador, claveSimetrica);
  const E_descripcion = encriptarSimetricoDatos("prueba", claveSimetrica);
  const notificacion: DTO_NotificationNatural = {
    titulo: "Prueba 01",
    autoridad: E_autoridad,
    descripcion: E_descripcion,
    notificador: E_notificador,
    notificados: E_notificados,
    enlaces: E_enlaces,
    formularioNotificacion: E_formularioNotificacion,
  };
  const sha = generarSHA256(notificacion);
  const formatData: DTO_MainNotification = {
    notificacion,
    seguridad: {
      iv: ivCifrado,
      llaveSimetrica: llaveSimetricaCifrada,
    },
    sha256: sha,
  };
  console.log("📦 Notificación generada:");
  console.log(JSON.stringify(formatData, null, 2));
};
