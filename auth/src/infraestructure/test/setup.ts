import { prisma } from "../config/prisma.client";

beforeAll(async () => {
  // Add any global setup here
});

afterAll(async () => {
  await prisma.$disconnect();
});
