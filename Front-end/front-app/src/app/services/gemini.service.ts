import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private apiUrl = 'http://localhost:8080/api/recibo';

  constructor(private http: HttpClient) {}

  uploadImages(files: File[]) {
    const formData = new FormData();
    for (const file of files) {
      formData.append('imagem', file);
    }
    return this.http.post<any>(`${this.apiUrl}/upload`, formData);
  }

  reviewData(dataId: string, dadosCorrigidos: any[]) {
    return this.http.post<any>(`${this.apiUrl}/review/${dataId}`, dadosCorrigidos);
  }

  downloadExcel(excelId: string) {
    return this.http.get(`${this.apiUrl}/download/${excelId}`, { responseType: 'blob' });
  }
}
