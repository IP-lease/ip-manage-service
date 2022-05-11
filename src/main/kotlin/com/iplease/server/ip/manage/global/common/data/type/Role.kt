package com.iplease.server.ip.release.global.common.data.type

enum class Role(
    vararg roles: Permission
) {
    GUEST,
    USER(
        Permission.IP_RELEASE_DEMAND,
        Permission.IP_RELEASE_DEMAND_CANCEL,
        Permission.IP_RELEASE_RESERVE,
        Permission.IP_RELEASE_RESERVE_CANCEL
    ),
    OPERATOR(*USER.roles.toTypedArray(), Permission.IP_RELEASE_ACCEPT),
    ADMINISTRATOR(*Permission.values());
    private val roles = roles.toSet()

    fun hasPermission(permission: Permission) = roles.contains(permission)
}
