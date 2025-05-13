interface ErrorWithMessage {
  message: string;
  code?: string;
  stack?: string;
}

function isErrorWithMessage(error: unknown): error is ErrorWithMessage {
  return (
    typeof error === "object" &&
    error !== null &&
    "message" in error &&
    typeof (error as Record<string, unknown>).message === "string"
  );
}

function toErrorWithMessage(maybeError: unknown): ErrorWithMessage {
  if (isErrorWithMessage(maybeError)) {
    return maybeError;
  }

  if (maybeError instanceof Error) {
    return {
      message: maybeError.message,
      stack: maybeError.stack,
    };
  }

  try {
    return {
      message: JSON.stringify(maybeError),
      code: "UNKNOWN_ERROR",
    };
  } catch {
    return {
      message: String(maybeError),
      code: "UNKNOWN_ERROR",
    };
  }
}

export function getErrorMessage(error: unknown): string {
  return toErrorWithMessage(error).message;
}

export function getFormattedError(error: unknown): ErrorWithMessage {
  const errorWithMessage = toErrorWithMessage(error);
  return {
    message: errorWithMessage.message,
    code: errorWithMessage.code || "UNKNOWN_ERROR",
    stack:
      process.env.NODE_ENV === "development"
        ? errorWithMessage.stack
        : undefined,
  };
}
