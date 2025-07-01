// import { randomBytes, createCipheriv, createDecipheriv } from "crypto";
// import config from "../config/config";

// const KEY = Buffer.from(config.token_secret_key, "utf-8");

// function generateIV() {
//   return randomBytes(16);
// }
// export interface TokenPayload {
//   id: string;
//   exp: number;
// }

// export function encryptPayload(payload: object): {
//   encryptedData: string;
//   iv: string;
// } {
//   const iv = generateIV();
//   const cipher = createCipheriv("aes-256-gcm", KEY, iv);

//   const json = JSON.stringify(payload);
//   let encrypted = cipher.update(json, "utf8", "base64");
//   encrypted += cipher.final("base64");

//   const authTag = cipher.getAuthTag().toString("base64");

//   return {
//     encryptedData: encrypted + "." + authTag,
//     iv: iv.toString("base64"),
//   };
// }

// export function decryptPayload<T = TokenPayload>(
//   encryptedData: string,
//   ivBase64: string
// ): T {
//   const [encrypted, authTagB64] = encryptedData.split(".");
//   const iv = Buffer.from(ivBase64, "base64");
//   const authTag = Buffer.from(authTagB64, "base64");

//   const decipher = createDecipheriv("aes-256-gcm", KEY, iv);
//   decipher.setAuthTag(authTag);

//   let decrypted = decipher.update(encrypted, "base64", "utf8");
//   decrypted += decipher.final("utf8");

//   return JSON.parse(decrypted) as T;
// }
