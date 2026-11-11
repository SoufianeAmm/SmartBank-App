import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Customer, CustomerRequest, Page } from './models';

/** EXEMPLE DE RÉFÉRENCE : un service Angular par microservice (accounts, transfers, loans...). */
@Injectable({ providedIn: 'root' })
export class CustomerApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/customers`;

  me(): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/me`);
  }

  list(page = 0, size = 20): Observable<Page<Customer>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Customer>>(this.baseUrl, { params });
  }

  get(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/${id}`);
  }

  create(request: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(this.baseUrl, request);
  }

  update(id: number, request: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${this.baseUrl}/${id}`, request);
  }
}
