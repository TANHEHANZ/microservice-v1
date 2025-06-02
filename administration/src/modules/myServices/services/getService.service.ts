import { prismaC } from "@/infraestructure/config/prisma.client";

export const getServiceService = async (): Promise<ResponseS> => {
  try {
    const service = await prismaC.service.findMany({
      where: {
        status: "ACTIVE",
      },
      include: {
        s_tipo: true,
        s_configuration: true,
        s_grupo: true,
      },
    });

    return {
      success: true,
      message: "Servicios obtenidos correctamente",
      data: service,
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al obtener los servicios",
      error: error instanceof Error ? error.message : "Error desconocido",
    };
  }
};
