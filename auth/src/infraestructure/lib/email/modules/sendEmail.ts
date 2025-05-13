import { getTransporter } from "../services/conection.email";

export const sendEmail = async (options: {
  to: string | string[];
  subject: string;
  html: string;
}) => {
  try {
    const transporter = getTransporter();
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      ...options,
    });
  } catch (error) {
    console.error("Failed to send email:", error);
    throw new Error("Email sending failed");
  }
};
