import { S_Redirect } from "../enum/redirect.enum";
import { PROXYTIMEOUT } from "./constants";

interface ServiceConfig {
  url: string;
  timeout: number;
  redirect: S_Redirect;
}

interface Services {
  [key: string]: ServiceConfig;
}

const config = {
  env: process.env.NODE_ENV || "development",
  port: parseInt(process.env.PORT || "3000"),
  debug: process.env.APP_DEBUG === "true",
  logLevel: process.env.LOG_LEVEL || "info",
  issuerBaseUrl: process.env.ISSUER_BASE_URL || "",
  audience: process.env.AUDIENCE || "",
  services: {
    auth: {
      url: process.env.AUTH_SERVICE_URL || "http://localhost:3002",
      timeout: parseInt(process.env.AUTH_TIMEOUT as string) || PROXYTIMEOUT,
      redirect: (process.env.AUTH_REDIRECT as S_Redirect) || S_Redirect.AUTH,
    },
    approvent: {
      url: process.env.APPROVENT_SERVICE_URL || "http://localhost:3003",
      timeout:
        parseInt(process.env.APPROVENT_TIMEOUT as string) || PROXYTIMEOUT,
      redirect:
        (process.env.APPROVENT_REDIRECT as S_Redirect) ||
        S_Redirect.APPROVEMENT,
    },
    signed: {
      url: process.env.SIGNED_GAMC || "http://localhost:3004",
      timeout: parseInt(process.env.SIGNED_TIMEOUT as string) || PROXYTIMEOUT,
      redirect:
        (process.env.SIGNED_REDIRECT as S_Redirect) || S_Redirect.SIGNED,
    },
  } as Services,
};

export default config;
