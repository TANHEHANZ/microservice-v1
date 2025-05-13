import { NextFunction, Request, Response } from "express";
import { UnauthorizedError } from "express-oauth2-jwt-bearer";
import config from "../config/config";
import { getErrorMessage } from "../utils/instanceError";
import CustomError from "../errors/customError";
import logger from "../config/logger";

export default function errorHandler(
  error: unknown,
  req: Request,
  res: Response,
  next: NextFunction
) {
  if (res.headersSent || config.debug) {
    next(error);
    return;
  }

  if (error instanceof CustomError) {
    res.status(error.statusCode).json({
      error: {
        message: error.message,
        code: error.code,
      },
    });
    return;
  }
  logger.error("Error occurred", {
    error: getErrorMessage(error),
    stack: error instanceof Error ? error.stack : undefined,
    path: req.path,
    method: req.method,
  });

  if (error instanceof UnauthorizedError) {
    res.status(error.statusCode).json({
      error: {
        message: error.message,
        code: "code" in error ? error.code : "ERR_AUTH",
      },
    });
    return;
  }

  res.status(500).json({
    error: {
      message:
        getErrorMessage(error) ||
        "An error occurred. Please view logs for more details",
    },
  });
}
