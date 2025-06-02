import { prismaC } from "@/infraestructure/config/prisma.client";
import { createServiceDTO } from "@/infraestructure/validator/service/service.model";

export const createServiceService = async (
  data: createServiceDTO
): Promise<ResponseS> => {
  try {
    const existingPermissions = await prismaC.permissons.findMany({
      where: {
        id: { in: data.s_configurationId },
      },
      select: {
        id: true,
      },
    });

    if (existingPermissions.length !== data.s_configurationId.length) {
      const missingIds = data.s_configurationId.filter(
        (id) => !existingPermissions.some((p) => p.id === id)
      );
      return {
        success: false,
        message: "Algunos permisos no existen",
        error: `IDs no encontrados: ${missingIds.join(", ")}`,
      };
    }

    const create = await prismaC.service.create({
      data: {
        name: data.name,
        description: data.description,
        icon: data.icon,
        s_tipo: data.s_tipoId ? { connect: { id: data.s_tipoId } } : undefined,
        s_grupo: data.s_grupoId
          ? { connect: { id: data.s_grupoId } }
          : undefined,
        s_configuration: {
          create: {
            permissons: {
              connect: data.s_configurationId.map((id) => ({ id })),
            },
          },
        },
      },
      include: {
        s_configuration: {
          include: {
            permissons: true,
          },
        },
      },
    });

    return {
      success: true,
      message: "Servicio creado correctamente",
      data: create,
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al crear el servicio",
      error: error instanceof Error ? error.message : "Error desconocido",
    };
  }
};
