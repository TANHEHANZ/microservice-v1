import { Router } from "express";
import passport from "passport";
import { handleGoogleAuthCallback } from "./google/controllers/handleGoogleAuthCallback";
const AuthenticacionRoutes = Router();
// AuthenticacionRoutes.get("/google", (req, res, next) => {
//   const origin = req.headers.referer || req.headers.origin;
//   passport.authenticate("google", {
//     scope: ["profile", "email"],
//     session: false,
//     state: JSON.stringify({ origin }),
//   })(req, res, next);
// });

AuthenticacionRoutes.get("/google", (req, res, next) => {
  const origin = req.headers.referer || req.headers.origin;
  const display = req.query.display === "popup" ? "popup" : "page";

  passport.authenticate("google", {
    scope: ["profile", "email"],
    session: false,
    state: JSON.stringify({ origin, display }),
    display: display, // Google OAuth parameter for popup
  })(req, res, next);
});

AuthenticacionRoutes.get("/google/callback", (req, res, next) => {
  passport.authenticate("google", { session: false }, async (err, user) => {
    await handleGoogleAuthCallback(err, user, req, res);
  })(req, res, next);
});

export default AuthenticacionRoutes;
