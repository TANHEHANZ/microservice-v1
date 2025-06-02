import dotenv from "dotenv";
dotenv.config();
import { createServer } from "./server";
import config from "./infraestructure/config/config";

const startServer = async () => {
  try {
    const server = createServer();

    server.listen(config.port, () => {
      console.log(`SERVICE-ADMIN Running on port: ${config.port}`);
    });
  } catch (error) {
    console.error("Failed to start server:", error);
    process.exit(1);
  }
};

startServer();
