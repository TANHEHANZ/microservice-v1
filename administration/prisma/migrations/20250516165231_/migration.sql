-- CreateEnum
CREATE TYPE "StatusEnum" AS ENUM ('ACTIVE', 'INACTIVE', 'BLOCKED', 'SUSPENDED', 'DEACTIVATED', 'PENDING_VERIFICATION', 'PASSWORD_EXPIRED', 'LOCKED', 'DELETED', 'EXTERNAL', 'ARCHIVED');

-- CreateTable
CREATE TABLE "Service" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "icon" TEXT NOT NULL,
    "status" "StatusEnum" NOT NULL DEFAULT 'ACTIVE',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "configId" TEXT,

    CONSTRAINT "Service_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "OpcionesService" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "icon" TEXT NOT NULL,
    "status" "StatusEnum" NOT NULL DEFAULT 'ACTIVE',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "serviceId" TEXT,
    "configId" TEXT,

    CONSTRAINT "OpcionesService_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ConfigurationService" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "congiguration" JSONB NOT NULL,
    "status" "StatusEnum" NOT NULL DEFAULT 'ACTIVE',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "ConfigurationService_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Service" ADD CONSTRAINT "Service_configId_fkey" FOREIGN KEY ("configId") REFERENCES "ConfigurationService"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "OpcionesService" ADD CONSTRAINT "OpcionesService_serviceId_fkey" FOREIGN KEY ("serviceId") REFERENCES "Service"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "OpcionesService" ADD CONSTRAINT "OpcionesService_configId_fkey" FOREIGN KEY ("configId") REFERENCES "ConfigurationService"("id") ON DELETE SET NULL ON UPDATE CASCADE;
