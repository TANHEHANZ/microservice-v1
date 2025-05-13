import { Strategy as GoogleStrategy } from "passport-google-oauth20";

import passport from "passport";
import config from "@/infraestructure/config/config";
import { formaterData } from "../formaterData";

export const configureGoogleStrategy = () => {
  passport.use(
    new GoogleStrategy(
      {
        clientID: config.GOOGLE_CLIENT_ID,
        clientSecret: config.GOOGLE_CLIENT_SECRET,
        callbackURL: config.GOOGLE_CALLBACK_URL,
        scope: ["profile", "email"],
        passReqToCallback: true,
      },
      formaterData
    )
  );
};
