import fs from "fs";
import path from "path";
const config = {
  env: process.env.NODE_ENV || "development",
  port: parseInt(process.env.PORT || "3000"),
  PEM: {
    NOTIFICATION: fs.readFileSync(
      path.resolve(process.cwd(), "src/.infra/key/NOTIFICATION.pem"),
      "utf8"
    ),
    AUTH: fs.readFileSync(
      path.resolve(process.cwd(), "src/.infra/key/AUTH.pem"),
      "utf8"
    ),
  },
  API: {
    notifications: process.env.URL_NOTIFICATION || "url Notifications",
    auth: process.env.URL_AUTORIZATION || "url Notifications",
  },
  token: {
    notifications: process.env.TOKEN_NOTIFICATION || "url Notifications",
    auth: process.env.TOKEN_AUTORIZATION || "url Notifications",
  },
};
export default config;
