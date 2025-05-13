import { Request, Response, NextFunction } from "express";
import { AuthError } from "../types/auth.error.type";

interface AuthResponse {
  user: any;
  error?: AuthError;
}

export const handleAuthResponse = (
  successRedirect: string,
  failureRedirect: string
) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const authResponse = req.user as AuthResponse;

    if (authResponse?.error) {
      return res.redirect(
        `${failureRedirect}?error=${encodeURIComponent(
          JSON.stringify(authResponse.error)
        )}`
      );
    }

    if (!authResponse?.user) {
      return res.redirect(`${failureRedirect}?error=authentication_failed`);
    }
    const token = "token";
    // const token = generateAuthToken(authResponse.user); // review formato de token
    res.redirect(`${successRedirect}?token=${token}`);
  };
};
