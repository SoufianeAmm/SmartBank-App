export const environment = {
  /** Toutes les requêtes API passent par l'API Gateway. */
  apiUrl: 'http://localhost:8080/api',
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'smartbank',
    clientId: 'smartbank-frontend',
  },
};
