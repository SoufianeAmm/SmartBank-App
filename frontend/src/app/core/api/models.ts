/** Format de page renvoyé par Spring Data (serialization-mode: via-dto). */
export interface Page<T> {
  content: T[];
  page: { size: number; number: number; totalElements: number; totalPages: number };
}

/** Format d'erreur commun (ProblemDetail RFC 7807) renvoyé par tous les services. */
export interface ApiError {
  status: number;
  detail: string;
  code?: string;
  errors?: Record<string, string>;
}

export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  nationalId: string;
  phone?: string;
  address?: string;
  dateOfBirth?: string;
  createdAt: string;
}

export type CustomerRequest = Omit<Customer, 'id' | 'createdAt'> & { keycloakUserId?: string };
