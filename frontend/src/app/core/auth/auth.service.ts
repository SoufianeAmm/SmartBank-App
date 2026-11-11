import { inject, Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

import { SmartbankRole } from './role.guard';

/** Infos sur l'utilisateur connecté (lues dans le token Keycloak). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = inject(Keycloak);

  get username(): string {
    return this.keycloak.tokenParsed?.['preferred_username'] ?? '';
  }

  get fullName(): string {
    return this.keycloak.tokenParsed?.['name'] ?? this.username;
  }

  get roles(): SmartbankRole[] {
    return (this.keycloak.realmAccess?.roles ?? []).filter((r): r is SmartbankRole =>
      ['CLIENT', 'AGENT', 'ADMIN'].includes(r),
    );
  }

  hasRole(...roles: SmartbankRole[]): boolean {
    return roles.some((r) => this.roles.includes(r));
  }

  logout(): Promise<void> {
    return this.keycloak.logout({ redirectUri: window.location.origin });
  }
}
