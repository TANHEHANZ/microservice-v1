import dotenv from "dotenv";
dotenv.config();
import config from "./config/config";
import { createServer } from "./server";

const server = createServer();

server.listen(config.port, () => {
  console.log(`API-GETWAY Run in :  ${config.port}`);
});
