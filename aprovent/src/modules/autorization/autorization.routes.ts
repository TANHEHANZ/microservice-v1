import { Router } from "express";
import { enviarNotificacionController } from "./persona_natural/controller/autorization.controller";

const auth_router = Router();
auth_router.post("/natural", enviarNotificacionController);

export default auth_router;
