import { Component, inject } from '@angular/core';

import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  template: `
    <h1>Bienvenue {{ auth.fullName }}</h1>
    <p>Rôles : {{ auth.roles.join(', ') || 'aucun' }}</p>
    <p class="hint">
      Point de départ du tableau de bord. Chaque équipe ajoute ses pages dans
      <code>src/app/features/</code> et ses routes dans <code>app.routes.ts</code>.
    </p>
  `,
})
export class Home {
  protected readonly auth = inject(AuthService);
}
