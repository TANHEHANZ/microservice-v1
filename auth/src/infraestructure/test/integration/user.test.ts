import {
  describe,
  expect,
  beforeAll,
  afterAll,
  beforeEach,
} from "@jest/globals";
import request from "supertest";
import { createServer } from "../../../server";
import { prisma } from "../../config/prisma.client";

const app = createServer();

describe("User API Integration Tests", () => {
  let statusId: string;
  let userTypeId: string;

  beforeAll(async () => {
    // First, check what statuses exist
    const allStatuses = await prisma.status.findMany();
    console.log("Available statuses:", allStatuses);

    const status = await prisma.status.findFirst();
    if (!status) throw new Error("No status found in database");
    statusId = status.id;

    // Check available user types
    const allUserTypes = await prisma.userType.findMany();
    console.log("Available user types:", allUserTypes);

    const userType = await prisma.userType.findFirst();
    if (!userType) throw new Error("No userType found in database");
    userTypeId = userType.id;
  });

  afterAll(async () => {
    await prisma.user.deleteMany();
    await prisma.$disconnect();
  });

  describe("POST /user", () => {
    const validUser = {
      email: "test@example.com",
      username: "testuser",
      password: "Test@123456",
      ci: "12345678",
      userTypeId: "",
      statusId: "",
    };

    beforeEach(() => {
      validUser.userTypeId = userTypeId;
      validUser.statusId = statusId;
    });

    test("should create a new user successfully", async () => {
      const response = await request(app).post("/user").send(validUser);

      expect(response.status).toBe(201);
      expect(response.body.message).toBe("Usuario creado correctamente");
      expect(response.body.data).toMatchObject({
        email: validUser.email,
        username: validUser.username,
        ci: validUser.ci,
      });
      expect(response.body.data.password).toBeUndefined();
    });

    test("should not create user with duplicate email", async () => {
      // First create a user
      await request(app).post("/user").send(validUser);

      // Try to create the same user again
      const response = await request(app).post("/user").send(validUser);

      expect(response.status).toBe(409);
      expect(response.body.message).toBe("Este usuario ya existe");
    });

    test("should validate required fields", async () => {
      const invalidUser = {
        email: "invalid-email",
        password: "123",
        userTypeId: "invalid-uuid",
      };

      const response = await request(app).post("/user").send(invalidUser);

      expect(response.status).toBe(409);
      expect(response.body.errors).toBeDefined();
    });
  });
});
