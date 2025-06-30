import crypto from "crypto";

export const calcularSha256 = (objeto: object): string => {
  const json = JSON.stringify(objeto);
  const hash = crypto.createHash("sha256").update(json).digest("hex");
  return hash;
};
