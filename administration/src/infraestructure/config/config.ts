const config = {
  env: process.env.NODE_ENV || "development",
  port: parseInt(process.env.PORT || "3000"),
  JWT_SECRET: process.env.JWT_SECRET || "key",
  hostRedis: process.env.HOST_REDIS || "localhost",
  portRedis: parseInt(process.env.PORT_REDIS || "6379"),
  passwordRedis: process.env.PASSWORD_REDIS || "pass",
  GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID || "",
  GOOGLE_CLIENT_SECRET: process.env.GOOGLE_CLIENT_SECRET || "",
  GOOGLE_CALLBACK_URL: process.env.GOOGLE_CALLBACK_URL || "",

  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || "1d",

  hostEmail: process.env.EMAIL_HOST || "smtp.gmail.com",
  portEmail: parseInt(process.env.EMAIL_PORT || "465"),
  secureEmail: process.env.EMAIL_SECURE === "false" ? false : true,
  userEmail: process.env.EMAIL_USER,
  passwordEmail: process.env.EMAIL_PASSWORD,
};
export default config;
