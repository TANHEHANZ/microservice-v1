import { Request, Response } from "express";
import { createProxyMiddleware } from "http-proxy-middleware";
import { ServiceType } from "../enum/services.enum";
import { Socket } from "net";
import config from "../config/config";
import logger from "../config/logger";

interface ProxyOptions {
  serviceType: ServiceType;
}
const handleProxyRequest = (proxyReq: any, req: Request, res: Response) => {
  if (req.body && Object.keys(req.body).length > 0) {
    const bodyData = JSON.stringify(req.body);
    proxyReq.setHeader("Content-Type", "application/json");
    proxyReq.setHeader("Content-Length", Buffer.byteLength(bodyData));
    proxyReq.write(bodyData);
  }

  logger.info(`Request completed`, {
    environment: config.env,
    ip: req.ip,
    method: req.method,
    path: proxyReq.path,
    service: `${req.baseUrl.split("/")[1]}-service`,
    statusCode: res.statusCode,
    timestamp: new Date().toISOString(),
    userAgent: req.get("user-agent"),
  });
};

const handleProxyResponse = (proxyRes: any, req: Request, res: Response) => {
  logger.info(`Request completed`, {
    environment: config.env,
    ip: req.ip,
    method: req.method,
    path: req.path,
    service: `${req.baseUrl.split("/")[1]}-service`,
    statusCode: proxyRes.statusCode,
    timestamp: new Date().toISOString(),
    userAgent: req.get("user-agent"),
  });
};
const handleError = (err: Error, req: Request, res: any) => {
  logger.error(`Microservice communication error`, {
    service: `${req.baseUrl.split("/")[1]}-service`,
    errorType: err.name,
    errorMessage: err.message,
    path: req.path,
    method: req.method,
  });

  if (!(res instanceof Socket)) {
    res.status(500).json({ error: "Error interno del servidor" });
  }
};

const createProxyOptions = ({ serviceType }: ProxyOptions) => ({
  target: config.services[serviceType].url,
  changeOrigin: true,
  pathRewrite: (path: string) =>
    path.replace("/", config.services[serviceType].redirect),
  proxyTimeout: config.services[serviceType].timeout,
  timeout: config.services[serviceType].timeout,
  on: {
    proxyReq: handleProxyRequest,
    proxyRes: handleProxyResponse,
    error: handleError,
  },
});

export const createServiceProxy = (options: ProxyOptions) => {
  return createProxyMiddleware(createProxyOptions(options));
};
