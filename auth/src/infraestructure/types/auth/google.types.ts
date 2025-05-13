import { Profile } from "passport-google-oauth20";

export interface GoogleAuthResult {
  user: Profile;
  callback: any;
}

export interface GoogleCallbackParams {
  profile: Profile;
  callback: any;
}
