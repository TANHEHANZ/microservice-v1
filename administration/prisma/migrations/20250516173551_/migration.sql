/*
  Warnings:

  - You are about to drop the column `serviceId` on the `OpcionesService` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "OpcionesService" DROP CONSTRAINT "OpcionesService_serviceId_fkey";

-- AlterTable
ALTER TABLE "OpcionesService" DROP COLUMN "serviceId";

-- AlterTable
ALTER TABLE "Service" ADD COLUMN     "OpcionesServiceId" TEXT;

-- AddForeignKey
ALTER TABLE "Service" ADD CONSTRAINT "Service_OpcionesServiceId_fkey" FOREIGN KEY ("OpcionesServiceId") REFERENCES "OpcionesService"("id") ON DELETE SET NULL ON UPDATE CASCADE;
