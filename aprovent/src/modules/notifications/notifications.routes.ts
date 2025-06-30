import { Router } from "express";
import { enviarNotificacionController } from "./persona_natural/controllers/notfication.controller";

const n_router = Router();

n_router.get("/", enviarNotificacionController);

export default n_router;
