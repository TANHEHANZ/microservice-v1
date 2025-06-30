import fs from "fs";
import path from "path";
const config = {
  env: process.env.NODE_ENV || "development",
  port: parseInt(process.env.PORT || "3000"),
  PEM: fs.readFileSync(
    path.resolve(process.cwd(), "src/.infra/key/Llave_publica_TEST.pem"),
    "utf8"
  ),
  API: {
    notifications: process.env.URL_NOTIFICATION || "url Notifications",
  },
  token: {
    notifications: process.env.TOKEN_NOTIFICATION || "url Notifications",
  },
};
export default config;
