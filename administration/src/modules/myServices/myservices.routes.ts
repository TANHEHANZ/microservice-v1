import { Router } from "express";
import { crudServiceController } from "./controllers/crud.controller";
import { GroupController } from "./controllers/group.controller";

const MyServices = Router();

MyServices.get("/all", crudServiceController.getAllServices)
  .post("/", crudServiceController.createService)
  .put("/", crudServiceController.putServices)
  .delete("/", crudServiceController.deletedServices);
MyServices.get("/group", GroupController.AllGroups)
  .post("/group", GroupController.createGroup)
  .put("/group", GroupController.putServices);

export default MyServices;
