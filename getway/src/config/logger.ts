import winston from "winston";
import DailyRotateFile from "winston-daily-rotate-file";
import config from "./config";
interface LogMetadata {
  service?: string;
  [key: string]: any;
}
const logLevels = {
  error: 0,
  warning: 1,
  info: 2,
  http: 3,
  debug: 4,
};

const logger = winston.createLogger({
  levels: logLevels,
  level: config.logLevel,
  defaultMeta: {
    service: "api-gateway",
    environment: config.env,
  },
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.metadata(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.printf(({ timestamp, level, message, metadata }) => {
          const meta = metadata as LogMetadata;
          const metaWithoutService = { ...meta };
          delete metaWithoutService.service;
          return `${timestamp} ${level}: ${message} ${JSON.stringify(
            meta,
            null,
            0
          )}`;
        })
      ),
    }),
    new DailyRotateFile({
      filename: "logs/error-%DATE%.log",
      datePattern: "YYYY-MM-DD",
      zippedArchive: true,
      maxSize: "20m",
      maxFiles: "14d",
      level: "error",
      format: winston.format.json(),
    }),
    new DailyRotateFile({
      filename: "logs/combined-%DATE%.log",
      datePattern: "YYYY-MM-DD",
      zippedArchive: true,
      maxSize: "20m",
      maxFiles: "14d",
      format: winston.format.json(),
    }),
  ],
});

export default logger;
