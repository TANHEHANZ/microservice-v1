import { randomBytes, randomUUID, scrypt, timingSafeEqual } from "crypto";
import { promisify } from "util";

const scryptAsync = promisify<string, string, number, Buffer>(scrypt);

export const hashSecret = async (secretToHash: string): Promise<string> => {
  const salt = randomBytes(16).toString("hex");
  const derivedKey = await scryptAsync(secretToHash, salt, 64);
  return `${salt}:${derivedKey.toString("hex")}`;
};

export const verifySecret = async (
  secretToVerify: string,
  hashedSecret: string
): Promise<boolean> => {
  const [salt, key] = hashedSecret.split(":");
  const keyBuffer = Buffer.from(key, "hex");
  const derivedKey = await scryptAsync(secretToVerify, salt, 64);
  return timingSafeEqual(keyBuffer, derivedKey);
};

export const generateClientId = (): string => {
  return `client_${randomUUID().replace(/-/g, "")}`;
};

export const generateClientSecret = (): string => {
  const bytes = randomBytes(64);
  const base64 = bytes.toString("base64url");
  return `secret_${base64}`;
};
export const generateClientCredentials = async () => {
  const clientId = generateClientId();
  const clientSecret = generateClientSecret();
  const hashedSecret = await hashSecret(clientSecret);

  return {
    clientId,
    clientSecret,
    hashedSecret,
  };
};
export const validateClientCredentials = async (
  clientSecret: string,
  storedHashedSecret: string
): Promise<boolean> => {
  console.log(clientSecret);
  console.log(storedHashedSecret);
  return await verifySecret(clientSecret, storedHashedSecret);
};
