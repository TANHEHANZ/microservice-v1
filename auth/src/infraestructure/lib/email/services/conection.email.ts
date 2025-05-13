import { Transporter } from "nodemailer";
import { confgNodeMiler } from "./config.service";

let transporter: Transporter | null = null;
let isConnected = false;

export const getTransporter = (): Transporter => {
  if (!isConnected || !transporter) {
    throw new Error("Email service not connected");
  }
  return transporter;
};

export const connectEmail = async (): Promise<void> => {
  try {
    transporter = confgNodeMiler();
    await transporter.verify();
    isConnected = true;
    console.log("Email service connected successfully");
  } catch (error) {
    isConnected = false;
    console.error("Email service connection failed:", error);
    throw new Error("Failed to connect to email service");
  }
};

export const verifyConnection = async (): Promise<boolean> => {
  try {
    if (!transporter) return false;
    await transporter.verify();
    return true;
  } catch (error) {
    console.error("Email service verification failed:", error);
    return false;
  }
};

export const disconnectEmail = async (): Promise<void> => {
  try {
    if (transporter) {
      await transporter.close();
      transporter = null;
      isConnected = false;
      console.log("Email service disconnected successfully");
    }
  } catch (error) {
    console.error("Error disconnecting email service:", error);
    throw new Error("Failed to disconnect email service");
  }
};
