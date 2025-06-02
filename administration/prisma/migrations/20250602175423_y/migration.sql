/*
  Warnings:

  - You are about to drop the column `OpcionesServiceId` on the `Service` table. All the data in the column will be lost.
  - You are about to drop the column `configId` on the `Service` table. All the data in the column will be lost.
  - You are about to drop the `ConfigurationService` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `OpcionesService` table. If the table is not empty, all the data it contains will be lost.
  - Added the required column `s_configurationId` to the `Service` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "OpcionesService" DROP CONSTRAINT "OpcionesService_configId_fkey";

-- DropForeignKey
ALTER TABLE "Service" DROP CONSTRAINT "Service_OpcionesServiceId_fkey";

-- DropForeignKey
ALTER TABLE "Service" DROP CONSTRAINT "Service_configId_fkey";

-- AlterTable
ALTER TABLE "Service" DROP COLUMN "OpcionesServiceId",
DROP COLUMN "configId",
ADD COLUMN     "s_configurationId" TEXT NOT NULL,
ADD COLUMN     "s_grupoId" TEXT,
ADD COLUMN     "s_tipoId" TEXT;

-- DropTable
DROP TABLE "ConfigurationService";

-- DropTable
DROP TABLE "OpcionesService";

-- CreateTable
CREATE TABLE "permissons" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "status" "StatusEnum" NOT NULL DEFAULT 'ACTIVE',
    "s_ConfigurationId" TEXT,

    CONSTRAINT "permissons_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "S_Grupo" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "S_Grupo_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "S_Configuration" (
    "id" TEXT NOT NULL,
    "permissonsId" TEXT NOT NULL,

    CONSTRAINT "S_Configuration_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "S_tipo" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "icon" TEXT NOT NULL,

    CONSTRAINT "S_tipo_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "permissons_name_key" ON "permissons"("name");

-- CreateIndex
CREATE INDEX "idx_permissons_name" ON "permissons"("name");

-- AddForeignKey
ALTER TABLE "Service" ADD CONSTRAINT "Service_s_tipoId_fkey" FOREIGN KEY ("s_tipoId") REFERENCES "S_tipo"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Service" ADD CONSTRAINT "Service_s_configurationId_fkey" FOREIGN KEY ("s_configurationId") REFERENCES "S_Configuration"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Service" ADD CONSTRAINT "Service_s_grupoId_fkey" FOREIGN KEY ("s_grupoId") REFERENCES "S_Grupo"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "S_Configuration" ADD CONSTRAINT "S_Configuration_permissonsId_fkey" FOREIGN KEY ("permissonsId") REFERENCES "permissons"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
