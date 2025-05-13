export const PermissionChecker = {
  hasPermission: (
    userPermissions: string[],
    requiredPermission: string
  ): boolean => {
    return userPermissions.includes(requiredPermission);
  },

  hasAnyPermission: (
    userPermissions: string[],
    requiredPermissions: string[]
  ): boolean => {
    return requiredPermissions.some((permission) =>
      userPermissions.includes(permission)
    );
  },

  hasAllPermissions: (
    userPermissions: string[],
    requiredPermissions: string[]
  ): boolean => {
    return requiredPermissions.every((permission) =>
      userPermissions.includes(permission)
    );
  },
};
