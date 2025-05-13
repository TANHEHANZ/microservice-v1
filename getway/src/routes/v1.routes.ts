import { Router } from "express";
import { createServiceProxy } from "../services/proxy.service";
import { ServiceType } from "../enum/services.enum";

const v1: Router = Router();

v1.use(
  "/auth",
  createServiceProxy({
    serviceType: ServiceType.AUTH,
  })
);
v1.use(
  "/approvent",
  createServiceProxy({
    serviceType: ServiceType.APPROVEMENT,
  })
);
v1.use(
  "/signed-gamc",
  createServiceProxy({
    serviceType: ServiceType.SIGNED,
  })
);
export default v1;
