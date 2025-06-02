import { prismaC } from "@/infraestructure/config/prisma.client";
import { createGroupDTO } from "@/infraestructure/validator/service/s_group.validator";

export const groupService = {
  all: async (): Promise<ResponseS> => {
    try {
      const groups = await prismaC.s_Grupo.findMany({
        include: {
          Service: {
            include: {
              s_configuration: true,
              s_tipo: true,
            },
          },
        },
      });
      if (groups.length === 0) {
        return {
          success: true,
          message: "No hay grupos",
          error: "No existe ningun grupo",
        };
      }
      return {
        success: true,
        message: "Grupos obtenidos correctamente",
        data: groups,
      };
    } catch (error) {
      return {
        success: false,
        message: "Error al obtener los grupos",
        error: "Contactar al administrador del sistema",
      };
    }
  },
  create: async (data: createGroupDTO): Promise<ResponseS> => {
    try {
      const group = await prismaC.s_Grupo.create({
        data: {
          name: data.name,
          status: "ACTIVE",
        },
      });
      return {
        success: true,
        message: "Grupo creado correctamente",
        data: group,
      };
    } catch (error) {
      return {
        success: false,
        message: "Error al crear el grupo",
        error: "Contactar al administrador del sistema",
      };
    }
  },
  groupServices: async (data: createGroupDTO): Promise<ResponseS> => {
    const ids = data.idService;
    const encontrados = await prismaC.service.findMany({
      where: { id: { in: ids } },
    });

    const idsEncontrados = encontrados.map((item) => item.id);
    const idsNoEncontrados = ids.filter((id) => !idsEncontrados.includes(id));
    if (idsNoEncontrados.length > 0) {
      return {
        success: false,
        message: "No se encuentran algunos servicios",
        data: idsNoEncontrados,
        error: "Revisa que los servicios existan",
      };
    }
    const updateService = await prismaC.service.updateMany({
      where: { id: { in: ids } },
      data: { s_grupoId: data.id_group },
    });
    if (!updateService) {
      return {
        success: false,
        message: "Error al agrupar servicios",
        error: "Contactar al administrador del sistema",
      };
    }
    return {
      success: true,
      message: "Servicios agrupados correctamente",
      data: updateService,
    };
  },
};
