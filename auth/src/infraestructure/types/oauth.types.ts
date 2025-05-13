export interface OAuthClient {
  client_id: string;
  redirect_uris: string[];
  allowed_scopes: string[];
}

export interface OAuthRequest {
  client_id: string;
  redirect_uri: string;
  scope?: string;
  state?: string;
  response_type: "code" | "token";
}

export interface OAuthTokenResponse {
  access_token: string;
  token_type: "Bearer";
  expires_in: number;
  refresh_token?: string;
  id_token?: string;
  scope?: string;
}
