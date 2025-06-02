import { Router } from "express";
import { crudServiceController } from "./controllers/crud.controller";

const MyServices = Router();

MyServices.get("/", crudServiceController.getAllServices);

export default MyServices;
