interface responseValidated {
  succes: boolean;
  message: string;
}

export const ValidatedAcountService = async (
  user: any
): Promise<responseValidated> => {
  //  consular con el otro microsrevicio si el usuario existe
  const permisson = "ADMINISTRADOR";
  const STATUS = "ACTIVO";
  if (STATUS === "ACTIVO") {
    return {
      succes: false,
      message: "Error este usuario se encuentra inactivo",
    };
  }
  if (permisson != "ADMINISTRADOR") {
    return {
      succes: false,
      message:
        "Error este usuario no puede acceder a este metodo de autenticacion ",
    };
  }
  return {
    succes: true,
    message: "Bienvenido",
  };
};
