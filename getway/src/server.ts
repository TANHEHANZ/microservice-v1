import express, { Request, Response } from "express";
import cors from "cors";
import config from "./config/config";
import morganMiddleware from "./middlewares/morgan-middleware";
import errorHandler from "./middlewares/error-handler";
import v1 from "./routes/v1.routes";
import { loggerMiddleware } from "./middlewares/logger-middleware";

export const createServer = () => {
  const app = express();
  app
    .disable("x-powered-by")
    .use(express.json())
    .use(cors())
    .use("/", v1)
    .use(morganMiddleware)
    .use(loggerMiddleware)
    .use(express.urlencoded({ extended: true }))
    .use(express.json());

  app.get("/health", (req: Request, res: Response) => {
    res.json({ ok: true, environment: config.env });
  });

  app.use(errorHandler);

  return app;
};
