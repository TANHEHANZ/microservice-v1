import { AuthMethods, Provider } from "@prisma/client";

export interface AuthPayload {
  id: string;
  authMethods: AuthMethods;
  currentProvider: Provider;
  role: {
    id: string;
    name: string;
    permissions: {
      id: string;
      name: string;
    }[];
  };
}
