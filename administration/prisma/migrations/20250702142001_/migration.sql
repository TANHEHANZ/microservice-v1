/*
  Warnings:

  - You are about to drop the `Nav` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `Service` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `User` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropForeignKey
ALTER TABLE "Nav" DROP CONSTRAINT "Nav_parentId_fkey";

-- DropForeignKey
ALTER TABLE "User" DROP CONSTRAINT "User_id_method_fkey";

-- DropForeignKey
ALTER TABLE "u_dispositivos" DROP CONSTRAINT "u_dispositivos_userId_fkey";

-- DropTable
DROP TABLE "Nav";

-- DropTable
DROP TABLE "Service";

-- DropTable
DROP TABLE "User";

-- CreateTable
CREATE TABLE "nav" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "path" TEXT NOT NULL,
    "icon" TEXT NOT NULL,
    "color" TEXT,
    "Status" "status" NOT NULL DEFAULT 'ACTIVO',
    "parentId" TEXT,

    CONSTRAINT "nav_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "service" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "icon" TEXT NOT NULL,
    "color" TEXT NOT NULL,
    "config" JSONB NOT NULL,
    "status" "status" NOT NULL DEFAULT 'ACTIVO',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "service_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user" (
    "id" TEXT NOT NULL,
    "name" TEXT,
    "ci" TEXT,
    "email" TEXT,
    "googleId" TEXT,
    "rol" "Role" NOT NULL DEFAULT 'USER',
    "id_method" TEXT NOT NULL,
    "permissions" TEXT[],
    "status" "status" NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT "user_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "user_ci_key" ON "user"("ci");

-- CreateIndex
CREATE UNIQUE INDEX "user_email_key" ON "user"("email");

-- CreateIndex
CREATE UNIQUE INDEX "user_googleId_key" ON "user"("googleId");

-- AddForeignKey
ALTER TABLE "nav" ADD CONSTRAINT "nav_parentId_fkey" FOREIGN KEY ("parentId") REFERENCES "nav"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_id_method_fkey" FOREIGN KEY ("id_method") REFERENCES "u_method"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "u_dispositivos" ADD CONSTRAINT "u_dispositivos_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
