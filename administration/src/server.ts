import express, { Request, Response } from "express";
import cors from "cors";
import cookieParser from "cookie-parser";
import path from "path";
import compression from "compression";
import router from "./modules/routes";

export const createServer = () => {
  const app = express();

  app.disable("x-powered-by").use(compression());
  app
    .use(express.urlencoded({ extended: true }))
    .use(express.json())
    .use(cors())
    .use(cookieParser())
    .use(
      "/static",
      express.static(path.join(__dirname, "public"), {
        maxAge: "1d",
        etag: true,
        lastModified: true,
        index: false,
        dotfiles: "ignore",
        immutable: true,
      })
    )
    .use("/v1/api/", router);

  app.get("/", (req: Request, res: Response) => {
    res.json({
      status: "success",
      message: "Authentication Service is running",
      timestamp: new Date().toISOString(),
    });
  });

  return app;
};
