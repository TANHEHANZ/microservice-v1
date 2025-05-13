import { validate } from "@/infraestructure/midlweware/validated";
import { Router } from "express";
// import { authClient } from "./controller/JWT.client";
// import { verificacionJWT } from "./controller/verificated";

const clientAuthRoute = Router();

// clientAuthRoute.post("/", authClient).post("/validate", verificacionJWT);

export default clientAuthRoute;
