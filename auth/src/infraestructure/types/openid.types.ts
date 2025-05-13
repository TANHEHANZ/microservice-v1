export interface UserInfo {
  sub: string; // Required
  name?: string;
  email?: string;
  email_verified?: boolean;
  picture?: string;
  role?: string;
}

export interface IDToken {
  iss: string; // Issuer
  sub: string; // Subject (user ID)
  aud: string; // Audience (client ID)
  exp: number; // Expiration time
  iat: number; // Issued at
  auth_time?: number; // Time of authentication
  nonce?: string;
  at_hash?: string; // Access token hash
}
