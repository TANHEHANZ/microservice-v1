import { Router } from "express";
import MyServices from "./myServices/myservices.routes";

const router = Router();
router.use("/services", MyServices);

export default router;
