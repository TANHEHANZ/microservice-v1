import { Request, Response, NextFunction } from "express";
import logger from "../config/logger";

export const loggerMiddleware = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const startTime = Date.now();
  const originalEnd = res.end;
  res.end = function (
    chunk?: any,
    encoding?: string | (() => void),
    cb?: () => void
  ): Response {
    const responseTime = Date.now() - startTime;
    logger.info("Request completed", {
      method: req.method,
      path: req.path,
      statusCode: res.statusCode,
      responseTime,
      ip: req.ip,
      userAgent: req.get("user-agent"),
    });
    return originalEnd.call(this, chunk, encoding as any, cb);
  };

  next();
};
