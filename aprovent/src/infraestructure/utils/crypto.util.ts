import { randomBytes } from "crypto";

export const generarClaveYIV = () => {
  const clave = randomBytes(32).toString("hex");
  const iv = randomBytes(16).toString("hex");
  return { clave, iv };
};
const llave_iv = generarClaveYIV();
console.log("Clave AES (32 bytes) en hexadecimal:", llave_iv.clave);
console.log("IV (16 bytes) en hexadecimal:", llave_iv.iv);
