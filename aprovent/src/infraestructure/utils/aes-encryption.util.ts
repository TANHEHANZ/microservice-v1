import { createCipheriv } from "crypto";

export interface LlaveSimetricoEntidad {
  llaveAES: string;
  ivAES: string;
}

export const encriptarSimetricoDatos = (
  texto: string,
  llaveSimetricoEntidad: LlaveSimetricoEntidad
): string => {
  const encriptado = aesEncrypt(
    texto,
    llaveSimetricoEntidad.llaveAES,
    llaveSimetricoEntidad.ivAES
  );
  return encriptado;
};

export const aesEncrypt = (
  cadena: string,
  aesLlave: string,
  aesIV: string
): string => {
  try {
    const key = Buffer.from(aesLlave, "hex");
    const iv = Buffer.from(aesIV, "hex");
    const cipher = createCipheriv("aes-256-cbc", key, iv);
    let encrypted = cipher.update(cadena, "utf8", "hex");
    encrypted += cipher.final("hex");
    return encrypted;
  } catch (error) {
    throw new Error(
      `No se pudo encriptar la cadena: ${(error as Error).message}`
    );
  }
};
import crypto from "crypto";

export function encriptarRSA(data: string, publicKeyPem: string): string {
  const buffer = Buffer.from(data, "utf8");
  const encrypted = crypto.publicEncrypt(publicKeyPem, buffer);
  return encrypted.toString("base64");
}
