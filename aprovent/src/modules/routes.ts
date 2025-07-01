import { Router } from "express";
import n_router from "./notifications/notifications.routes";
import auth_router from "./autorization/autorization.routes";
import approvals_router from "./approvals/approvals.routes";
const router = Router();
router.use("/approvals", approvals_router);
router.use("/notification", n_router);
router.use("/autotization", auth_router);

export default router;
