import passport from "passport";
import { configureGoogleStrategy } from "./strategies/google.strategy";

export const initializePassport = () => {
  configureGoogleStrategy();
};

export default passport;
