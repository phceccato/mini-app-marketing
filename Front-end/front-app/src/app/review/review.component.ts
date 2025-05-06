import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css']
})
export class ReviewComponent {
  @Input() data: any[] = [];
  @Input() dataId: string = '';
  excelId: string = '';
  gerando = false;
  erro = '';

  constructor(private http: HttpClient) {}

  gerarExcel(): void {
    if (!this.dataId || this.data.length === 0) {
      this.erro = 'Sem dados para gerar Excel.';
      return;
    }

    this.gerando = true;
    this.erro = '';

    this.http.post<{ excelId: string }>(
      `http://localhost:8080/api/recibo/review/${this.dataId}`,
      this.data
    ).subscribe({
      next: (res) => {
        this.excelId = res.excelId;
        this.gerando = false;
      },
      error: (err) => {
        this.erro = 'Erro ao gerar planilha.';
        this.gerando = false;
      }
    });
  }

  baixarExcel(): void {
    if (!this.excelId) return;

    const link = document.createElement('a');
    link.href = `http://localhost:8080/api/recibo/download/${this.excelId}`;
    link.download = 'planilha.xlsx';
    link.click();
  }
}
