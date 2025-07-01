import config from "@/infraestructure/config/config";
import {
  encriptarRSA,
  encriptarSimetricoDatos,
} from "@/infraestructure/utils/aes-encryption.util";
import { generarClaveYIV } from "@/infraestructure/utils/crypto.util";
import { A_notificado, A_notificador } from "../mocks";
import { DTO_MainAutorization } from "../validators/v_autorization";
import { generarSHA256 } from "@/infraestructure/utils/sha256";

export const enviarAuthorization = async () => {
  const { clave, iv } = generarClaveYIV();

  const llaveSimetricaCifrada = encriptarRSA(clave, config.PEM.AUTH);
  const ivCifrado = encriptarRSA(iv, config.PEM.AUTH);
  const claveSimetrica = {
    llaveAES: clave,
    ivAES: iv,
  };
  const descripcion = "Pruebaaaaaaaa";
  const E_notificador = encriptarSimetricoDatos(A_notificador, claveSimetrica);
  const E_notificado = encriptarSimetricoDatos(A_notificado, claveSimetrica);
  const E_descripcion = encriptarSimetricoDatos(descripcion, claveSimetrica);
  const autorizacion = {
    titulo: "Autorizacion de prueba",
    descripcion: E_descripcion,
    notificador: E_notificador,
    notificado: E_notificado,
  };
  const sha = generarSHA256(autorizacion);

  const formatData: DTO_MainAutorization = {
    autorizacion,
    seguridad: {
      iv: ivCifrado,
      llaveSimetrica: llaveSimetricaCifrada,
    },
    sha256: sha,
  };
  console.log(JSON.stringify(formatData, null, 2));
};
