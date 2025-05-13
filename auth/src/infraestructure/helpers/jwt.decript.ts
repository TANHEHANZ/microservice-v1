import { createPrivateKey, privateDecrypt, constants } from "crypto";
import fs from "fs";
import path from "path";
import jwt, { TokenExpiredError, JsonWebTokenError } from "jsonwebtoken";

const MASTER_PRIVATE_KEY = createPrivateKey(
  fs.readFileSync(path.join(__dirname, "../../../master_private.pem"))
);

export const decryptToken = async (token: string) => {
  try {
    const decoded = jwt.decode(token, { complete: true });

    if (!decoded || !decoded.payload || !(decoded.payload as any).data) {
      throw new Error("Invalid token format");
    }
    const decryptedPayload = privateDecrypt(
      {
        key: MASTER_PRIVATE_KEY,
        padding: constants.RSA_PKCS1_OAEP_PADDING,
        oaepHash: "sha256",
      },
      Buffer.from((decoded.payload as any).data, "base64")
    );

    const payload = JSON.parse(decryptedPayload.toString());

    const exp = (decoded.payload as any).exp;
    if (exp && Date.now() >= exp * 1000) {
      throw new TokenExpiredError("Token expired", new Date(exp * 1000));
    }

    return {
      ...payload,
      exp: exp ? new Date(exp * 1000) : undefined,
    };
  } catch (error) {
    if (error instanceof TokenExpiredError) {
      throw new Error("Token has expired");
    } else if (error instanceof JsonWebTokenError) {
      throw new Error("Invalid token");
    }
    console.error("Token decryption failed:", error);
    throw new Error("Invalid token");
  }
};
