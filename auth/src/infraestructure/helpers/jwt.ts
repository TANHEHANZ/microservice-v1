import {
  createPublicKey,
  randomBytes,
  publicEncrypt,
  constants,
  createCipheriv,
} from "crypto";
import fs from "fs";
import path from "path";
import jwt from "jsonwebtoken";

const MASTER_PUBLIC_KEY = createPublicKey(
  fs.readFileSync(path.join(__dirname, "../../../master_public.pem"))
);

export const generateSecureToken = async (clientData: {
  client_id: string;
  scopes?: string[];
  expiresIn?: any;
}) => {
  try {
    const payload = {
      client_id: clientData.client_id,
      scopes: clientData.scopes || [],
      type: "Bearer",
    };

    const encryptedPayload = publicEncrypt(
      {
        key: MASTER_PUBLIC_KEY,
        padding: constants.RSA_PKCS1_OAEP_PADDING,
        oaepHash: "sha256",
      },
      Buffer.from(JSON.stringify(payload))
    ).toString("base64");

    const token = jwt.sign({ data: encryptedPayload }, randomBytes(32), {
      algorithm: "HS256",
      expiresIn: clientData.expiresIn || "1h",
    });

    return token;
  } catch (error) {
    console.error("Token generation failed:", error);
    throw error;
  }
};
