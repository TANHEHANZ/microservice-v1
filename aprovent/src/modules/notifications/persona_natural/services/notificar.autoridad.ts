import {
  cifrarAES,
  cifrarClaveConRSA,
  generarIvYClave,
} from "@/infraestructure/utils/aes-encryption.util";
import { calcularSha256 } from "@/infraestructure/utils/hash.util";
import http from "@/infraestructure/utils/http-client";

export const enviarNotificacion = async (data: any) => {
  try {
    const { clave, iv } = generarIvYClave();

    const notificadorPayload = {
      numeroDocumento: "8369155",
      fechaNacimiento: "2001-01-04",
      tipoDocumento: "CI",
    };
    const notificadorEncriptado = cifrarAES(
      JSON.stringify(notificadorPayload),
      clave,
      iv
    );

    const notificacion = {
      titulo: cifrarAES("Título de prueba", clave, iv),
      descripcion: cifrarAES("Mensaje de prueba", clave, iv),
      notificador: notificadorEncriptado,
      notificados: [notificadorEncriptado],
      formularioNotificacion: {
        etiqueta: "form-prueba",
        url: cifrarAES("https://midocumento.pdf", clave, iv),
        tipo: "APROBACION",
        hash: cifrarAES("HASH_DOCUMENTO", clave, iv),
      },
      datosAdicionalesEntidad: [{ clave: "proceso", valor: "penal" }],
      enlaces: [],
    };
    const llaveSimetricaCifrada = cifrarClaveConRSA(clave);
    const ivCifrada = cifrarClaveConRSA(iv);

    const body = {
      notificacion,
      seguridad: {
        llaveSimetrica: llaveSimetricaCifrada,
        iv: ivCifrada,
      },
      sha256: calcularSha256(notificacion),
    };
    console.log(JSON.stringify(body, null, 2));

    const response = await http.post("/notificacion/natural", body);
    return response.data;
  } catch (error: any) {
    throw {
      mensaje: "Fallo al enviar la notificación a Ciudadanía Digital",
      detalle: error.response?.data || error.message || error,
      status: error.status || 500,
    };
  }
};
