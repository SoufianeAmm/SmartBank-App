import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forbidden',
  imports: [RouterLink],
  template: `
    <h1>Accès refusé</h1>
    <p>Vous n'avez pas le rôle nécessaire pour accéder à cette page.</p>
    <a routerLink="/">Retour à l'accueil</a>
  `,
})
export class Forbidden {}
