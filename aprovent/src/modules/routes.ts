import { Router } from "express";
import n_router from "./notifications/notifications.routes";
const router = Router();
router.use("/entity-public", n_router);
export default router;
