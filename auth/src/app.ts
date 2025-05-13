import dotenv from "dotenv";
dotenv.config();
import { createServer } from "./server";
import config from "./infraestructure/config/config";
import { connectEmail } from "./infraestructure/lib/email/services/conection.email";

const startServer = async () => {
  try {
    const server = createServer();
    // await connectEmail();
    server.listen(config.port, () => {
      console.log(`SERVICE-AUTH Running on port: ${config.port}`);
    });
  } catch (error) {
    console.error("Failed to start server:", error);
    process.exit(1);
  }
};

startServer();
