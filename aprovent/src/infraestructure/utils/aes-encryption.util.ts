import crypto from "crypto";
import config from "../config/config";

export const generarIvYClave = () => {
  const clave = crypto.randomBytes(32).toString("hex");
  const iv = crypto.randomBytes(16).toString("hex");
  return { clave, iv };
};

export const cifrarAES = (
  texto: string,
  claveHex: string,
  ivHex: string
): string => {
  const key = Buffer.from(claveHex, "hex");
  const iv = Buffer.from(ivHex, "hex");
  const cipher = crypto.createCipheriv("aes-256-cbc", key, iv);
  let cifrado = cipher.update(texto, "utf8", "hex");
  cifrado += cipher.final("hex");
  return cifrado;
};

export const cifrarClaveConRSA = (claveHex: string): string => {
  const claveBuffer = Buffer.from(claveHex, "hex");
  const claveCifrada = crypto.publicEncrypt(
    {
      key: config.PEM,
      padding: crypto.constants.RSA_PKCS1_OAEP_PADDING,
      oaepHash: "sha256",
    },
    claveBuffer
  );
  return claveCifrada.toString("base64");
};
