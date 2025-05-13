import { Router } from "express";
import AuthenticacionRoutes from "./authentication/authentication.routes";

const router = Router();

router.use("/authentication", AuthenticacionRoutes);
export default router;
