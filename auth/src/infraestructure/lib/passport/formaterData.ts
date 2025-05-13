import { Profile } from "passport-google-oauth20";

import { getErrorMessage } from "@/infraestructure/helpers/handler.error";
import { getIp } from "@/infraestructure/helpers/getIp";

export const formaterData = async (
  req: any,
  accessToken: string,
  refreshToken: string,
  profile: Profile,
  done: (error: any, user?: any) => void
) => {
  const state =
    typeof req.query.state === "string" ? JSON.parse(req.query.state) : {};
  try {
    const ip = getIp(req);
    const { origin } = state;
    const googleUserData = {
      email: profile.emails?.[0]?.value,
      firstName: profile.name?.givenName,
      lastName: profile.name?.familyName,
      profilePicture: profile.photos?.[0]?.value,
      providerId: profile.id,
      ip,
      origin,
      accessToken,
      refreshToken,
    };

    return done(null, googleUserData);
  } catch (error) {
    const errorMessage = getErrorMessage(error);
    return done(null, {
      error: true,
      message: errorMessage,
      origin: state?.origin,
    });
  }
};
