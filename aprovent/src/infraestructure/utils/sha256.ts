import crypto, { createHash } from "crypto";

import path from "path";
import fs from "fs";

/**
 * Genera un hash SHA-256 en formato hexadecimal a partir de un objeto.
 * @param data Objeto del cual se generará el SHA-256 (por ejemplo: notificacion)
 * @returns Hash SHA-256 como string hexadecimal
 */
export function generarSHA256(data: object): string {
  const jsonString = JSON.stringify(data);
  return crypto.createHash("sha256").update(jsonString).digest("hex");
}

export function generarSHA(data: string): string {
  return crypto.createHash("sha256").update(data).digest("hex");
}

export function obtenerHashArchivo(nombreArchivo: string) {
  const rutaArchivo = path.resolve(__dirname, "../../upload", nombreArchivo);

  const data = fs.readFileSync(rutaArchivo);
  return createHash("sha256").update(data).digest("hex");
}
