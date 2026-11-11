import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthGuardData, createAuthGuard } from 'keycloak-angular';

export type SmartbankRole = 'CLIENT' | 'AGENT' | 'ADMIN';

/**
 * Usage dans les routes :
 *   { path: 'customers', canActivate: [roleGuard], data: { roles: ['AGENT', 'ADMIN'] }, ... }
 * Sans data.roles, la route exige seulement d'être connecté.
 */
const isAccessAllowed = async (
  route: ActivatedRouteSnapshot,
  _state: RouterStateSnapshot,
  authData: AuthGuardData,
) => {
  const { authenticated, grantedRoles, keycloak } = authData;
  if (!authenticated) {
    await keycloak.login();
    return false;
  }
  const required = (route.data['roles'] as SmartbankRole[] | undefined) ?? [];
  if (required.length === 0 || required.some((r) => grantedRoles.realmRoles.includes(r))) {
    return true;
  }
  return inject(Router).parseUrl('/forbidden');
};

export const roleGuard = createAuthGuard<CanActivateFn>(isAccessAllowed);
