import { User, Role, OAuthClient } from "@prisma/client";

export interface InternalUser extends User {
  role?: Role;
  authMethods: AuthMethod[];
}

export interface AuthMethod {
  provider: string;
  provider_id?: string;
  extra_data?: any;
}
