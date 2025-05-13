import config from "@/infraestructure/config/config";
import nodemailer, { Transporter, TransportOptions } from "nodemailer";

export const confgNodeMiler = (): Transporter => {
  return nodemailer.createTransport({
    service: "smtp",
    host: config.hostEmail,
    port: config.portEmail,
    secure: config.secureEmail,
    auth: {
      user: config.userEmail,
      pass: config.passwordEmail,
    },
    pool: true,
    maxConnections: 5,
    maxMessages: 100,
  } as TransportOptions);
};
