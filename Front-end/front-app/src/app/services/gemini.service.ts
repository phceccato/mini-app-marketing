import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

// arquivo para interagir com uma API backend
@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private apiUrl = 'http://localhost:8080/api/recibo';

  constructor(private http: HttpClient) {}

  // Upload de imagens
  uploadImages(files: File[]) {
    const formData = new FormData();
    for (const file of files) {
      formData.append('imagem', file);
    }
    return this.http.post<any>(`${this.apiUrl}/upload`, formData);
  }

  // Revisão de dados
  reviewData(dataId: string, dadosCorrigidos: any[]) {
    return this.http.post<any>(`${this.apiUrl}/review/${dataId}`, dadosCorrigidos);
  }

  // Download de arquivos Excel
  downloadExcel(excelId: string) {
    return this.http.get(`${this.apiUrl}/download/${excelId}`, { responseType: 'blob' });
  }
}
