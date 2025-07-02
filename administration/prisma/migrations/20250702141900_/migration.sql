/*
  Warnings:

  - You are about to drop the column `s_configurationId` on the `Service` table. All the data in the column will be lost.
  - You are about to drop the column `s_grupoId` on the `Service` table. All the data in the column will be lost.
  - You are about to drop the column `s_tipoId` on the `Service` table. All the data in the column will be lost.
  - The `status` column on the `Service` table would be dropped and recreated. This will lead to data loss if there is data in the column.
  - You are about to drop the `S_Configuration` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `S_Grupo` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `S_tipo` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `permissons` table. If the table is not empty, all the data it contains will be lost.
  - Added the required column `color` to the `Service` table without a default value. This is not possible if the table is not empty.
  - Added the required column `config` to the `Service` table without a default value. This is not possible if the table is not empty.

*/
-- CreateEnum
CREATE TYPE "Role" AS ENUM ('ADMIN', 'CLIENT', 'USER');

-- CreateEnum
CREATE TYPE "status" AS ENUM ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'SUSPENDIDO', 'DESACTIVADO', 'PENDIENTE', 'EXPIRADO', 'ELIMINADO');

-- DropForeignKey
ALTER TABLE "S_Configuration" DROP CONSTRAINT "S_Configuration_permissonsId_fkey";

-- DropForeignKey
ALTER TABLE "Service" DROP CONSTRAINT "Service_s_configurationId_fkey";

-- DropForeignKey
ALTER TABLE "Service" DROP CONSTRAINT "Service_s_grupoId_fkey";

-- DropForeignKey
ALTER TABLE "Service" DROP CONSTRAINT "Service_s_tipoId_fkey";

-- AlterTable
ALTER TABLE "Service" DROP COLUMN "s_configurationId",
DROP COLUMN "s_grupoId",
DROP COLUMN "s_tipoId",
ADD COLUMN     "color" TEXT NOT NULL,
ADD COLUMN     "config" JSONB NOT NULL,
DROP COLUMN "status",
ADD COLUMN     "status" "status" NOT NULL DEFAULT 'ACTIVO';

-- DropTable
DROP TABLE "S_Configuration";

-- DropTable
DROP TABLE "S_Grupo";

-- DropTable
DROP TABLE "S_tipo";

-- DropTable
DROP TABLE "permissons";

-- DropEnum
DROP TYPE "StatusEnum";

-- CreateTable
CREATE TABLE "Nav" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "path" TEXT NOT NULL,
    "icon" TEXT NOT NULL,
    "color" TEXT,
    "Status" "status" NOT NULL DEFAULT 'ACTIVO',
    "parentId" TEXT,

    CONSTRAINT "Nav_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "User" (
    "id" TEXT NOT NULL,
    "name" TEXT,
    "ci" TEXT,
    "email" TEXT,
    "googleId" TEXT,
    "rol" "Role" NOT NULL DEFAULT 'USER',
    "id_method" TEXT NOT NULL,
    "permissions" TEXT[],
    "status" "status" NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "u_dispositivos" (
    "id" TEXT NOT NULL,
    "ip" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "confianza" BOOLEAN NOT NULL DEFAULT false,
    "userAgent" TEXT,
    "location" TEXT,
    "deviceType" TEXT,
    "status" "status" NOT NULL DEFAULT 'ACTIVO',
    "lastAccess" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "u_dispositivos_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "u_method" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "dataMethod" JSONB,
    "status" "status" NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT "u_method_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "oauth_clients" (
    "id" TEXT NOT NULL,
    "client_id" TEXT,
    "client_secret" TEXT,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "redirect_uris" TEXT[],
    "webhook_url" TEXT,
    "domain" TEXT,
    "Status" "status" NOT NULL DEFAULT 'ACTIVO',
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "oauth_clients_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Scope" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "permissions" TEXT[],
    "Status" "status" NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT "Scope_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "oauth_client_scopes" (
    "clientId" TEXT NOT NULL,
    "scopeId" TEXT NOT NULL,

    CONSTRAINT "oauth_client_scopes_pkey" PRIMARY KEY ("clientId","scopeId")
);

-- CreateTable
CREATE TABLE "Aprovent" (
    "id" TEXT NOT NULL,
    "status" "status" NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT "Aprovent_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_ci_key" ON "User"("ci");

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- CreateIndex
CREATE UNIQUE INDEX "User_googleId_key" ON "User"("googleId");

-- CreateIndex
CREATE UNIQUE INDEX "oauth_clients_client_id_key" ON "oauth_clients"("client_id");

-- CreateIndex
CREATE UNIQUE INDEX "Scope_name_key" ON "Scope"("name");

-- AddForeignKey
ALTER TABLE "Nav" ADD CONSTRAINT "Nav_parentId_fkey" FOREIGN KEY ("parentId") REFERENCES "Nav"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "User" ADD CONSTRAINT "User_id_method_fkey" FOREIGN KEY ("id_method") REFERENCES "u_method"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "u_dispositivos" ADD CONSTRAINT "u_dispositivos_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "oauth_client_scopes" ADD CONSTRAINT "oauth_client_scopes_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "oauth_clients"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "oauth_client_scopes" ADD CONSTRAINT "oauth_client_scopes_scopeId_fkey" FOREIGN KEY ("scopeId") REFERENCES "Scope"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
